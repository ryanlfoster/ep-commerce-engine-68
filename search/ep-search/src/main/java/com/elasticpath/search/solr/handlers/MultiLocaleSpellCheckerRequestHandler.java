package com.elasticpath.search.solr.handlers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrException.ErrorCode;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrQueryResponse;
import org.apache.solr.util.plugin.SolrCoreAware;

import com.elasticpath.service.search.solr.SpellingConstants.SpellingCmds;
import com.elasticpath.service.search.solr.SpellingConstants.SpellingParams;

/**
 * Takes a string (e.g. a query string) as a value of the {@link SolrParams#Q} parameter and looks
 * up alternate spelling suggestions in the spellchecker. Specify multiple "q" values for multiple
 * lookups. Each lookup is made on the specified locale in the "locale" parameter.
 */
@SuppressWarnings("PMD.FinalFieldCouldBeStatic")
public class MultiLocaleSpellCheckerRequestHandler extends RequestHandlerBase implements SolrCoreAware {
	
	private static final Logger LOG = Logger.getLogger(MultiLocaleSpellCheckerRequestHandler.class);
	
	private static final Pattern LOCALE_SPLIT_PATTERN = Pattern.compile("_");
	
	private static final String LOCALE_RETRIEVE_REGEX = "\\|(.*?)\\|.*$";
	
	private static final float DEFAULT_ACCURACY = 0.5f;

	private static final int DEFAULT_NUM_SUGGESTIONS = 1;
	
	private Map<Locale, SpellChecker> spellCheckerMap;
	
	private Map<Locale, Directory> spellCheckerIndexDir;
	
	private String dirDescription = "(ramdir)";
	
	private File directory = null;
	
	/*
	 * From http://wiki.apache.org/jakarta-lucene/SpellChecker
	 * If reader and restrictToField are both not null:
	 * 1. The returned words are restricted only to the words presents in the field
	 * "restrictToField "of the Lucene Index "reader".
	 *
	 * 2. The list is also sorted with a second criterium: the popularity (the
	 * frequence) of the word in the user field.
	 *
	 * 3. If "onlyMorePopular" is true and the misspelled word exist in the user field,
	 * return only the words more frequent than this.
	 * 
	 */
	private final IndexReader nullReader = null;
	
	private final String restrictToField = null;
	
	private final boolean onlyMorePopular = false;
	
	/**
	 * Initializes the request handler.
	 *
	 * @param args passed arguments
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void init(final NamedList args) {
		super.init(args);
		spellCheckerMap = new LinkedHashMap<Locale, SpellChecker>();
		spellCheckerIndexDir = new LinkedHashMap<Locale, Directory>();
	}

	/**
	 *	Called by Solr after initialization to let the handler know about the core. Finishes
	 *	initializing the request handler.   
	 *
	 * @param solrCore the core that uses the new instance
	 */
	@SuppressWarnings({ "PMD.AvoidThrowingRawExceptionTypes", "PMD.EmptyCatchBlock" })
	public void inform(final SolrCore solrCore) {
		final SolrParams params = SolrParams.toSolrParams(initArgs);
		
		final String dir = params.get("spellcheckerIndexDir");
		if (dir == null) {
			LOG.info("Using RAM based spell directory");
		} else {
			File file = new File(dir);
			if (!file.isAbsolute()) {
				file = new File(solrCore.getDataDir(), dir);
			}
			dirDescription = file.getAbsolutePath();
			directory = file;
			LOG.info("Using spell directory: " + dirDescription);
			
			// Open spell checkers for all sub-directories
			final String[] fileList = file.list();
			if (fileList != null) {
				for (String fileName : file.list()) {
					File folder = new File(file, fileName);
					if (!folder.isDirectory()) {
						continue;
					}
					
					try {
						Locale locale = parseLocale(fileName);
						SpellChecker spellChecker = new SpellChecker(getDirectory(locale));
						spellCheckerMap.put(locale, spellChecker);
					} catch (SolrException e) {
						// do nothing, ignore folders that don't conform to locale syntax
					} catch (IOException e) {
						throw new RuntimeException("Cannot open SpellChecker index.", e);
					}
				}
			}
		}
	}
	
	/**
	 * Handles the main request.
	 *
	 * @param req the main request
	 * @param rsp response to be written to
	 * @throws Exception in case of any errors
	 */
	@Override
	public void handleRequestBody(final SolrQueryRequest req, final SolrQueryResponse rsp) throws Exception {
		final SolrParams params = req.getParams();
		String cmd = params.get(SpellingParams.CMD);

		final String localeString = params.get(SpellingParams.LOCALE);
		Locale locale = null;
		if (localeString != null) {
			locale = parseLocale(localeString);
		}

		if (cmd != null) {
			cmd = cmd.trim();
			if (cmd.equals(SpellingCmds.REBUILD)) {
				rebuild(req);
				rsp.add("cmdExecuted", SpellingCmds.REBUILD);
			} else if (cmd.equals(SpellingCmds.REOPEN)) {
				checkLocale(locale);
				reopen(locale);
				rsp.add("cmdExecuted", SpellingCmds.REOPEN);
			} else {
				throw new SolrException(ErrorCode.BAD_REQUEST, "Unrecognized command: " + cmd);
			}
			return;
		}
		
		checkLocale(locale);
		final SpellChecker spellChecker = spellCheckerMap.get(locale);
		final String[] words = params.getParams(SpellingParams.QUERY);
		final NamedList<List<String>> perWordSuggestions = new NamedList<List<String>>();

		if (spellChecker != null) {
			Float accuracy;
			int numSugggestions;
			try {
				accuracy = params.getFloat(SpellingParams.ACCURACY, DEFAULT_ACCURACY);
				spellChecker.setAccuracy(accuracy);
			} catch (NumberFormatException e) {
				throw new SolrException(ErrorCode.BAD_REQUEST, "Accuracy must be a valid positive float", e);
			}
			try {
				numSugggestions = params.getInt(SpellingParams.NUM_SUGGESTIONS, DEFAULT_NUM_SUGGESTIONS);
			} catch (NumberFormatException e) {
				throw new SolrException(ErrorCode.BAD_REQUEST, "Spelling suggestion count must be a valid positive integer", e);
			}

			for (String word : words) {
				final String[] suggestions = suggest(word, spellChecker, numSugggestions);
				perWordSuggestions.add(word, Arrays.asList(suggestions));
			}
		}
		rsp.add("suggestions", perWordSuggestions);
	}

	private void checkLocale(final Locale locale) {
		if (locale == null) {
			throw new SolrException(ErrorCode.BAD_REQUEST, "Missing required field 'locale'");
		}
	}
	
	@SuppressWarnings("PMD.PreserveStackTrace")
	private Directory getDirectory(final Locale locale) throws SolrException {
		Directory spellDirectory = spellCheckerIndexDir.get(locale);
		if (spellDirectory == null) {
			if (directory == null) {
				spellDirectory = new RAMDirectory();
			} else {
				try {
					spellDirectory = FSDirectory.getDirectory(new File(directory, locale.toString()));
				} catch (IOException e) {
					throw new SolrException(ErrorCode.SERVER_ERROR, "Unable to open index directory", e);
				}
			}
			spellCheckerIndexDir.put(locale, spellDirectory);
		}
		return spellDirectory;
	}
	
	private void reopen(final Locale locale) throws IOException {
		final SpellChecker spellChecker = spellCheckerMap.get(locale);
		if (spellChecker != null) {
			spellChecker.setSpellIndex(getDirectory(locale));
		}
	}

	private void rebuild(final SolrQueryRequest req) throws IOException {
		@SuppressWarnings("unchecked")
		final Collection<String> fieldNames = req.getSearcher().getReader().getFieldNames(FieldOption.INDEXED);
		final Map<Locale, Boolean> localeHasBeenCleared = new LinkedHashMap<Locale, Boolean>();
		final Pattern localePattern = Pattern.compile(LOCALE_RETRIEVE_REGEX);
		for (String fieldName : fieldNames) {
			final Matcher localeMatcher = localePattern.matcher(fieldName);
			if (localeMatcher.find()) {
				final String matchedLocale = localeMatcher.group(1);
				Locale fieldLocale;
				try {
					fieldLocale = parseLocale(matchedLocale);
				} catch (SolrException e) {
					throw new SolrException(ErrorCode.SERVER_ERROR, String.format(
							"Unable to determine locale from field name <%1$S>, attempted to parse <%2$S>", fieldName,
							matchedLocale), e);
				}
				indexField(req, localeHasBeenCleared, fieldName, fieldLocale);
			} else if (fieldName.endsWith("_st")) {
				for (Locale locale : spellCheckerMap.keySet()) {
					indexField(req, localeHasBeenCleared, fieldName, locale);
				}
			}
		}
		for (Locale locale : spellCheckerMap.keySet()) {
			reopen(locale);
		}
	}

	private void indexField(final SolrQueryRequest req, final Map<Locale, Boolean> localeHasBeenCleared, final String fieldName,
			final Locale fieldLocale) throws IOException {
		final Dictionary dictionary = new LuceneDictionary(req.getSearcher().getReader(), fieldName);
		SpellChecker spellChecker = spellCheckerMap.get(fieldLocale);
		if (spellChecker == null) {
			spellChecker = new SpellChecker(getDirectory(fieldLocale));
			spellCheckerMap.put(fieldLocale, spellChecker);
		}
		// we only want to clear the locale once
		if (localeHasBeenCleared.get(fieldLocale) == null || !localeHasBeenCleared.get(fieldLocale)) {
			spellChecker.clearIndex();
			localeHasBeenCleared.put(fieldLocale, true);
		}
		spellChecker.indexDictionary(dictionary);
	}
	
	private String[] suggest(final String word, final SpellChecker spellChecker, final int numSuggestions) throws IOException {
		if (word != null && word.trim().length() != 0) {
			return spellChecker.suggestSimilar(word, numSuggestions, nullReader, restrictToField, onlyMorePopular);
		}
		return new String[0];
	}
	
	private Locale parseLocale(final String locale) {
		final String[] splitLocale = LOCALE_SPLIT_PATTERN.split(locale, 3);
		if (splitLocale.length == 0) {
			throw new SolrException(ErrorCode.BAD_REQUEST, "Unable to parse locale: " + locale);
		} else if (splitLocale.length == 1) {
			return new Locale(splitLocale[0]);
		} else if (splitLocale.length == 2) {
			return new Locale(splitLocale[0], splitLocale[1]);
		}
		return new Locale(splitLocale[0], splitLocale[1], splitLocale[2]);
	}

	/**
	 * Returns the version as a string.
	 *
	 * @return the version
	 */
	@Override
	public String getVersion() {
		return "$Revision: 1 $";
	}
	
	/**
	 * Returns the description of the handler.
	 *
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return "The Multi-Locale SpellChecker Solr request handler for SpellChecker index: " + dirDescription;
	}
	
	/**
	 * The source ID of the request handler.
	 *
	 * @return the source ID of the request handler
	 */
	@Override
	public String getSourceId() {
		return "$Id: MultiLocaleSpellCheckerRequestHandler.java 1 2007-09-28 aschurman $";
	}
	
	/**
	 * The source of the request handler.
	 *
	 * @return the source of the request handler
	 */
	@Override
	public String getSource() {
		return null;
	}
}
