package com.elasticpath.ql.parser;

import static com.elasticpath.ql.asserts.ParseAssert.assertParseInvalid;
import static com.elasticpath.ql.asserts.ParseAssert.assertParseSuccessfull;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.ql.custom.catalog.CatalogConfiguration;
import com.elasticpath.ql.custom.category.CategoryConfiguration;
import com.elasticpath.ql.custom.product.AttributeFieldResolver;
import com.elasticpath.ql.custom.product.PriceFieldResolver;
import com.elasticpath.ql.custom.product.ProductConfiguration;
import com.elasticpath.ql.custom.promotion.PromotionConfiguration;
import com.elasticpath.ql.custom.setting.MetadataFieldResolver;
import com.elasticpath.ql.custom.setting.SQLSettingSubQueryBuilder;
import com.elasticpath.ql.custom.setting.SQLSettingValueResolver;
import com.elasticpath.ql.custom.setting.SettingConfiguration;
import com.elasticpath.ql.parser.fieldresolver.impl.LocalizedFieldResolver;
import com.elasticpath.ql.parser.fieldresolver.impl.NonLocalizedFieldResolver;
import com.elasticpath.ql.parser.gen.EpQueryParserImpl;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLQueryBuilder;
import com.elasticpath.ql.parser.querybuilder.impl.JPQLSubQueryBuilder;
import com.elasticpath.ql.parser.querybuilder.impl.LuceneQueryBuilder;
import com.elasticpath.ql.parser.querybuilder.impl.LuceneSubQueryBuilder;
import com.elasticpath.ql.parser.querybuilder.impl.SQLQueryBuilder;
import com.elasticpath.ql.parser.valueresolver.impl.JPQLValueResolver;
import com.elasticpath.ql.parser.valueresolver.impl.LuceneValueResolver;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.IndexUtility;
import com.elasticpath.service.search.solr.IndexUtilityImpl;

/**
 * Test cases for JavaCC based implementation of EpQueryParser.
 */
public class EpQueryParserTest { // NOPMD

	private EpQueryParser queryParser;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		queryParser = new EpQueryParserImpl();

		EpQueryAssembler epQueryAssembler = new EpQueryAssembler();

		IndexUtility indexUtility = new IndexUtilityImpl();
		
		LuceneSubQueryBuilder conventionalQueryBuilder = new LuceneSubQueryBuilder();
		Map<EPQueryType, AbstractEpQLCustomConfiguration> epQLObjectConfiruration = new HashMap<EPQueryType, AbstractEpQLCustomConfiguration>();

		LocalizedFieldResolver localizedFieldResolver = new LocalizedFieldResolver();
		NonLocalizedFieldResolver nonLocalizedFieldResolver = new NonLocalizedFieldResolver();
		LuceneValueResolver conventionalValueResolver = new LuceneValueResolver();
		conventionalValueResolver.setAnalyzer(new AnalyzerImpl());
		localizedFieldResolver.setIndexUtility(indexUtility);
		AttributeFieldResolver attributeFieldResolver = new AttributeFieldResolver();
		attributeFieldResolver.setIndexUtility(indexUtility);
		PriceFieldResolver priceFieldResolver = new PriceFieldResolver();
		priceFieldResolver.setIndexUtility(indexUtility);		
		epQueryAssembler.setEpQLObjectConfiguration(epQLObjectConfiruration);

		CategoryConfiguration categoryConf = new CategoryConfiguration();
		categoryConf.setLocalizedFieldResolver(localizedFieldResolver);
		categoryConf.setNonLocalizedFieldResolver(nonLocalizedFieldResolver);
		categoryConf.setEpQLValueResolver(conventionalValueResolver);
		categoryConf.setSubQueryBuilder(conventionalQueryBuilder);
		categoryConf.setCompleteQueryBuilder(new LuceneQueryBuilder());
		categoryConf.initialize();
		epQLObjectConfiruration.put(EPQueryType.CATEGORY, categoryConf);

		ProductConfiguration productConf = new ProductConfiguration();
		productConf.setAttributeFieldResolver(attributeFieldResolver);
		productConf.setLocalizedFieldResolver(localizedFieldResolver);
		productConf.setNonLocalizedFieldResolver(nonLocalizedFieldResolver);
		productConf.setEpQLValueResolver(conventionalValueResolver);
		productConf.setSubQueryBuilder(conventionalQueryBuilder);
		productConf.setPriceFieldResolver(priceFieldResolver);
		productConf.setCompleteQueryBuilder(new LuceneQueryBuilder());
		productConf.initialize();
		epQLObjectConfiruration.put(EPQueryType.PRODUCT, productConf);

		CatalogConfiguration catalogConf = new CatalogConfiguration();
		catalogConf.setNonLocalizedFieldResolver(nonLocalizedFieldResolver);
		catalogConf.setEpQLValueResolver(new JPQLValueResolver());
		catalogConf.setSubQueryBuilder(new JPQLSubQueryBuilder());
		catalogConf.setCompleteQueryBuilder(new JPQLQueryBuilder());
		catalogConf.initialize();
		epQLObjectConfiruration.put(EPQueryType.CATALOG, catalogConf);

		PromotionConfiguration promoConf = new PromotionConfiguration();
		promoConf.setNonLocalizedFieldResolver(nonLocalizedFieldResolver);
		promoConf.setEpQLValueResolver(conventionalValueResolver);
		promoConf.setSubQueryBuilder(conventionalQueryBuilder);
		promoConf.setCompleteQueryBuilder(new LuceneQueryBuilder());
		promoConf.initialize();
		epQLObjectConfiruration.put(EPQueryType.PROMOTION, promoConf);
		
		SettingConfiguration configurationConf = new SettingConfiguration();
		configurationConf.setNonLocalizedFieldResolver(nonLocalizedFieldResolver);
		MetadataFieldResolver metadataFieldResolver = new MetadataFieldResolver();
		configurationConf.setMetadataFieldResolver(metadataFieldResolver);
		configurationConf.setEpQLValueResolver(new SQLSettingValueResolver());
		configurationConf.setSubQueryBuilder(new SQLSettingSubQueryBuilder());
		configurationConf.setCompleteQueryBuilder(new SQLQueryBuilder());
		configurationConf.initialize();
		epQLObjectConfiruration.put(EPQueryType.CONFIGURATION, configurationConf);
	
		queryParser.setEpQueryAssembler(epQueryAssembler);

	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse001() {
		assertParseSuccessfull("FIND Category WHERE CategoryCode='Tennis' AND CatalogCode='Sports'", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse002() {
		assertParseSuccessfull("FIND Category WHERE CategoryCode='Tennis' OR CategoryCode='Hockey' && CatalogCode='Sports'", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse003() {
		assertEquals("+(+(+catalogCode:\"Sports\") +(+categoryName|en|:\"Hockey\"))", assertParseSuccessfull(
				"FIND Category WHERE CatalogCode='Sports' AND CategoryName[en]='Hockey'", queryParser).toString());
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse004() {
		EpQuery epQuery = assertParseSuccessfull("FIND Category LIMIT 10", queryParser);
		final Integer ten = Integer.valueOf(10);
		assertEquals(ten, epQuery.getLimit());
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse005() {
		EpQuery epQuery = assertParseSuccessfull("FIND Category START 10", queryParser);
		assertEquals(EpQuery.LIMIT_NOT_SPECIFIED, epQuery.getLimit().intValue());
		final Integer ten = Integer.valueOf(10);
		assertEquals(ten, epQuery.getStartIndex());
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse006() {
		EpQuery epQuery = assertParseSuccessfull("FIND Category START 10 LIMIT 10", queryParser);
		final Integer ten = Integer.valueOf(10);
		assertEquals(ten, epQuery.getLimit());
		assertEquals(ten, epQuery.getStartIndex());
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse007() {
		assertParseSuccessfull("FIND Category WHERE((((CategoryCode != 'aaa'))))OR(CategoryName[en]='bbb')", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse008() {
		assertParseSuccessfull("        FIND   Category", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse009() {
		assertParseSuccessfull("FIND Category", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse010() {
		assertParseSuccessfull("FIND Product", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse011() {
		assertParseSuccessfull("FIND Category WHERE CategoryCode = '\"' OR CategoryCode = 'aaa\\'bbb' OR CategoryCode = '\\\\'", 
				queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse014() {
		assertParseSuccessfull("FIND Product WHERE NOT ProductCode='1234'", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse016() {
		assertParseSuccessfull("FIND Product WHERE NOT(NOT ProductCode!='1234')", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse017() {
		assertParseSuccessfull("FIND Catalog", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse018() {
		assertParseSuccessfull("FIND Catalog WHERE CatalogCode!='1' AND CatalogCode!='2' OR (CatalogCode='3' AND CatalogCode='4')", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testSuccessfulParse019() {
		assertParseSuccessfull("FIND Category LIMIT 10 START 10", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testSuccessfulParse020() {
		assertParseSuccessfull("FIND Category LIMIT 10 START 10", queryParser);
	}
	
	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testSuccessfulParse021() {
		assertParseSuccessfull("FIND Promotion WHERE State=ACTIVE AND CatalogCode='Sports' AND StoreCode='Sports store' " 
				+ "AND (PromotionName='TennisDiscountAmountOff' OR PromotionName='TennisPercentOffDiscount')", queryParser);
	}
	
	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse022() {
		assertParseSuccessfull("FIND Configuration WHERE Namespace='Tennis' AND Context='Sports'", queryParser);
	}
	
	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Valid queries.
	 */
	@Test
	public void testSuccessfulParse023() {
		assertParseSuccessfull("FIND Configuration WHERE Namespace='Tennis' AND Context='Sports' AND MetadataKey{bb}='ss'", queryParser);
	}
	
	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse002() {
		assertParseInvalid("FIND Attribute", "Invalid type", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse004() {
		assertParseInvalid("FIND Product WHERE ProductName='aaa'", "Locale must be specified", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse005() {
		assertParseInvalid("FIND Category WHERE AttributeName{Color}='aaa'", "No way to specify attribute for a category", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse006() {
		assertParseInvalid("FIND Category LIMIT", "No value for limit", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse007() {
		assertParseInvalid("SELECT Product", "expected FIND", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse008() {
		assertParseInvalid("FIND Category WHERE (CategoryCode != 'aaa' OR ) CategoryName[en]='bbb'", "", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse009() {
		assertParseInvalid("FIND Category WHERE (CategoryCode != 'aaa' OR CategoryName[en]='bbb'", "", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse010() {
		assertParseInvalid("FIND Category WHERE CategoryCode != \"aaa\" OR CategoryName[en]='bbb'", "", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse011() {
		assertParseInvalid("FIND Category WHERE CategoryCode='aaa'bbb'", " ' symbol should be escaped", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse012() {
		assertParseInvalid("FIND Category WHERE CategoryCode=='aab'", "==", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse013() {
		assertParseInvalid("FIND Category WHERE CategoryCode{wrong}='aab'", "Parameter2 should be removed", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse014() {
		assertParseInvalid("FIND Category WHERE CategoryCode[en]='aab'", "Parameter1 (locale) should be removed", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse015() {
		assertParseInvalid("FIND Category WHERE SomeCompletelyUnknownField='aab'", 
				"SomeCompletelyUnknownField is not in scope of EP QL fields", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse016() {
		assertParseInvalid("FIND Product WHERE Price{SNAPITUP} = '150'", "Parameter1 (currency) should be specified", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse017() {
		assertParseInvalid("FIND Product WHERE Price[CAD] = '150'", "Parameter2 (store code) should be specified", queryParser);
	}

	/**
	 * Tests that value closed in single quotes isn't alowed for Price field.
	 */
	@Test
	public void testInvalidParse018() {
		assertParseInvalid("FIND Product WHERE Price{SNAPITUP}[CAD] >= '250'", "Only numeric value is alowed for Price", queryParser);
	}

	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse019() {
		assertParseInvalid("FIND Catalog WHERE ProductCode='1'", "ProductCode is not correct field", queryParser);
	}
	
	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse020() {
		assertParseInvalid("FIND Promotion WHERE State='ACTIVE'", "State shouldn't be quoted", queryParser);
	}
	
	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Invalid queries.
	 */
	@Test
	public void testInvalidParse021() {
		assertParseInvalid("FIND Promotion WHERE State=SOME_INCORRECT_STATE", "Invalid State", queryParser);
	}
	
	/**
	 * Test method for {@link com.elasticpath.ql.parser.EpQueryParser#parse(java.lang.String)}. Attributes.
	 */
	@Test
	public void testAttributes() throws Exception {
		queryParser.verify("FIND Product WHERE AttributeName{quality}[en]='Good'");
	}

}
