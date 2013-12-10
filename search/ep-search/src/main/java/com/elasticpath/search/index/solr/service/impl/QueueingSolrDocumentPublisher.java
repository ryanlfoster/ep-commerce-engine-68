package com.elasticpath.search.index.solr.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;
import com.elasticpath.search.index.solr.queueingpublisher.impl.DeleteCommand;
import com.elasticpath.search.index.solr.queueingpublisher.impl.ShutdownCommand;
import com.elasticpath.search.index.solr.queueingpublisher.impl.SolrPublishCommand;
import com.elasticpath.search.index.solr.queueingpublisher.impl.UpdateCommand;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrDocumentPublisher;

/**
 * This implementation of a {@code SolrDocumentPublisher} will spin off a background thread which it uses to add documents asynchronously.
 * It is prototype scoped, with one publisher created for each {@code IndexType}.
 * 
 * <B>Important!</b> This class was initially designed to support periodic commits, however that feature is no longer used.  Periodic
 * commits interfer with rebuilding indexes.  Since we drop the index and re-add everything, a commit before the index is completely
 * re-added will cause things to 'disappear' from the active index.  
 * 
 * It is <i>very likely</i> that since we do <b>not</b> do periodic commits that area of index building can be replaced by using a
 * {@code StreamingUpdateSolrServer} instead as long as you honour commit requests through the {@code SolrDocumentPublisher#commit()} call.
 */
public class QueueingSolrDocumentPublisher implements SolrDocumentPublisher {

	private static final int DEFAULT_QUEUE_SIZE = 15000;

	/**
	 * One short of the maximum size that the thread will pull from the queue at once.
	 */
	private static final int QUEUE_DRAIN_SIZE = 499;

	private static final int BUFFER_SIZE = 510;

	private SolrServer solrServer;

	private TaskExecutor taskExecutor;

	private PipelinePerformance performance;

	private BlockingQueue<SolrPublishCommand> documentQueue;

	private static final Logger LOG = Logger.getLogger(QueueingSolrDocumentPublisher.class);

	private int documentQueueSize = DEFAULT_QUEUE_SIZE;

	private int queueDrainSize = QUEUE_DRAIN_SIZE;

	private int bufferSize = BUFFER_SIZE;

	/**
	 * Used by {@code #isBusy()} and the working thread. Not busy is defined as the working thread being blocked in {@code BlockingQueue#take()} and
	 * the associated queue being empty. This lock freezes the state to do this check.
	 */
	private final Lock busyLock = new ReentrantLock(true);

	/**
	 * Consumes the Solr document queue and publishes to the Solr server.
	 */
	class PublisherTask implements Runnable {

		/**
		 * Consumes the Solr document queue and publishes to the Solr server.
		 */
		public void run() {

			if (solrServer instanceof CommonsHttpSolrServer) {
				Thread.currentThread().setName("QueueingPublisher to " + ((CommonsHttpSolrServer) solrServer).getBaseURL());
			} else {
				Thread.currentThread().setName("QueueingPublisher talking to " + solrServer);
			}

			final List<SolrPublishCommand> localCopy = new ArrayList<SolrPublishCommand>(bufferSize);

			for (;;) {

				/**
				 * Drain as much of the queue as we can (up to a total of QUEUE_DRAIN_SIZE+1. Calling .take() on a blocking queue will block until
				 * there is something on the queue. If the queue has less than half of a QUEUE_DRAIN_SIZE on it, we sleep for DRAIN_WAIT_MS and then
				 * attempt our drain. This is really so that if the queue is full-enough, we don't bother waiting.
				 */

				try {
					localCopy.add(documentQueue.take());
					busyLock.lock();
					try {
						if (!handleDocuments(localCopy)) {
							return;
						}
					} finally {
						busyLock.unlock();
					}
				} catch (final InterruptedException e1) {
					LOG.warn("Interrupted while waiting on document queue.take(), continuing.", e1);
				}

			}
		}

		private boolean handleDocuments(final List<SolrPublishCommand> localCopy) {
			documentQueue.drainTo(localCopy, queueDrainSize);
			getPipelinePerformance().addCount("publish:documents_in", localCopy.size());

			final long start = System.currentTimeMillis();

			for (final SolrPublishCommand cmd : localCopy) {
				try {

					cmd.apply(solrServer);

				} catch (final InterruptedException ie) {
					Thread.currentThread().interrupt();
					LOG.info("Shutdown requested.");
					return false;

				} catch (final Exception e) {
					LOG.error("Error processing command.", e);
				}
			}
			if (solrServer instanceof CommonsHttpSolrServer) {
				LOG.info("Updated " + localCopy.size() + " documents against " + ((CommonsHttpSolrServer) solrServer).getBaseURL());
			}
			getPipelinePerformance().addValue("publish:time", System.currentTimeMillis() - start);
			getPipelinePerformance().addCount("publish:documents_out", localCopy.size());

			localCopy.clear();
			return true;
		}

	}

	/** Called by Spring to initialize this bean. */
	public void initialize() {
		this.documentQueue = new ArrayBlockingQueue<SolrPublishCommand>(documentQueueSize);

	}

	@Override
	public void start() {
		taskExecutor.execute(new PublisherTask());
	}

	@Override
	public void shutdown() {
		documentQueue.add(new ShutdownCommand());
	}

	/**
	 * Adds an update command for the given {@link SolrInputDocument} to the document queue for the specified index type.
	 * 
	 * @param indexType the domain type of the Solr server.
	 * @param document the document to add to the queue.
	 * @throws InterruptedException from {@code BlockingQueue#put(Object)}
	 */
	public void addUpdate(final IndexType indexType, final SolrInputDocument document) throws InterruptedException {
		documentQueue.put(new UpdateCommand(document));
	}

	/**
	 * Adds a delete command to the document queue for the specified index type and domain object identifier.
	 * 
	 * @param indexType the domain type of the Solr server.
	 * @param uid the domain object identifier
	 * @throws InterruptedException from {@code BlockingQueue#put(Object)}
	 */
	public void deleteDocument(final IndexType indexType, final long uid) throws InterruptedException {
		documentQueue.put(new DeleteCommand(uid));
	}

	/**
	 * The queueing publisher does not honour flushing on demand.
	 */
	public void flush() {
		// This publisher does not support flushing on demand.
	}

	@Override
	public void commit() {
		try {
			solrServer.commit();
		} catch (final Exception e) {
			LOG.fatal(
					"Exception while attempting to commit to Solr server "
							+ solrServer
							+ "; some documents may not appear in search queries. Rebuild recommended.",
					e);
		}
	}

	@Override
	public boolean isBusy() {

		if (taskExecutor instanceof SyncTaskExecutor) {
			return false;
		} else if (taskExecutor instanceof ThreadPoolTaskExecutor) {
			busyLock.lock();
			try {
				return !documentQueue.isEmpty();
			} finally {
				busyLock.unlock();
			}
		} else {
			throw new EpSystemException("The implementation of the taskExecutor (which is " + taskExecutor.getClass()
					+ ") for this class is not known, so we don't know how to check if it's busy or not.");
		}
	}

	public void setSolrServer(final SolrServer solrServer) {
		this.solrServer = solrServer;
	}

	public SolrServer getSolrServer() {
		return solrServer;
	}

	public void setDocumentPublisherTask(final TaskExecutor documentPublisherTask) {
		this.taskExecutor = documentPublisherTask;
	}

	public PipelinePerformance getPipelinePerformance() {
		return performance;
	}

	public void setPipelinePerformance(final PipelinePerformance performance) {
		this.performance = performance;
	}

	/**
	 * Control the size of the queue that this publisher pulls from. If you're creating documents faster than this publisher can publish them, then
	 * you'll want to grow this value.
	 * 
	 * @param documentQueueSize number of documents allowed in the queue
	 */
	public void setDocumentQueueSize(final int documentQueueSize) {
		this.documentQueueSize = documentQueueSize;
	}

	/**
	 * The maximum number of documents that will be removed from the queue at once. 
	 * 
	 * @param queueDrainSize documents to pull off the queue
	 */
	protected void setQueueDrainSize(final int queueDrainSize) {
		this.queueDrainSize = queueDrainSize;
	}

	/**
	 * Initial size for the array used to hold local copies of {@code SolrInputDocument}. Set this slightly larger than {@code #queueDrainSize} if
	 * needed.
	 * 
	 * @param bufferSize initial collection size
	 */
	protected void setBufferSize(final int bufferSize) {
		this.bufferSize = bufferSize;
	}
}
