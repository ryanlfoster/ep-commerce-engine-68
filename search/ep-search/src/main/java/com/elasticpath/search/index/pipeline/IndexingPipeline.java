package com.elasticpath.search.index.pipeline;

/**
 * An indexing pipeline is an ordered list of {@code IndexingStage}s. Each stage has an input and an output. The IndexPipeline is responsible for
 * joining the {@code IndexStages} together, using {@code IndexingStage#setNextStage(IndexingStage)}. The pipeline is started by calling
 * {@code #start(INITIALPAYLOAD)}.
 * 
 * @param <INITIALPAYLOAD> This is the payload expected to start the pipeline. This is usually a List<Long> of uids.
 * @param <ENTITYTYPE> This is the domain object that the pipeline is responsible for creating indexes for, it's used with {@code EntityLoadingStage}
 *            .
 *            <p>
 * @see IndexingTask
 * @see IndexingStage
 */
public interface IndexingPipeline<INITIALPAYLOAD, ENTITYTYPE> {

	/**
	 * Callers must call this method before using start for the first time. This is usually done using Spring during bean creation.
	 */
	void initialize();

	/**
	 * Callers must call this method to shutdown the pipeline. Work in the pipeline will continue to be processed, but no new work will be accepted.
	 * This method will block until the pipeline is shutdown. This is usually called using Spring during bean destruction.
	 */
	void destroy();

	/**
	 * Start the pipeline, passing in the specified data of type INITIALPAYLOAD.
	 * 
	 * @param startData data to start the indexing pipeline. Likely a List<Long> of uids.
	 */
	void start(INITIALPAYLOAD startData);

	/**
	 * A pipeline is busy if any of the stages in the pipeline are busy. This method can return false positives if needed but must not return false
	 * negatives.
	 * 
	 * @return boolean if it's busy or not.
	 */
	boolean isBusy();

}
