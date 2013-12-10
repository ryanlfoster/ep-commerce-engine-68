package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.productcategories.ProductCategoryAdapter;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoriesDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.catalog.ProductService;

/**
 * Exporter implementation for product category exporter.
 */
public class ProductCategoryExporterImpl extends AbstractExporterImpl<Product, ProductCategoriesDTO, Long> {

	private ProductCategoryAdapter productCategoryAdapter;

	private ProductService productService;

	private ProductLoadTuner productLoadTuner;

	@Override
	protected void initializeExporter(final ExportContext context) {
		// do nothing
	}

	@Override
	protected List<Product> findByIDs(final List<Long> subList) {
		return productService.findByUids(subList, productLoadTuner);
	}

	@Override
	protected DomainAdapter<Product, ProductCategoriesDTO> getDomainAdapter() {
		return productCategoryAdapter;
	}

	@Override
	protected Class<? extends ProductCategoriesDTO> getDtoClass() {
		return ProductCategoriesDTO.class;
	}

	@Override
	public JobType getJobType() {
		return JobType.PRODUCTCATEGORYASSOCIATION;
	}

	@Override
	protected List<Long> getListExportableIDs() {
		return new ArrayList<Long>(getContext().getDependencyRegistry().getDependentUids(Product.class));
	}

	@Override
	public Class< ? >[] getDependentClasses() {
		return new Class< ? >[] { Product.class };
	}

	@Override
	protected void addDependencies(final List<Product> objects, final DependencyRegistry dependencyRegistry) {
		if (dependencyRegistry.supportsDependency(Category.class)) {
			addCategoryDependency(objects, dependencyRegistry);
		}
	}

	private void addCategoryDependency(final List<Product> objects, final DependencyRegistry dependencyRegistry) {
		Set<Long> categorySet = new HashSet<Long>();
		for (Product product : objects) {
			for (Category category : product.getCategories()) {
				categorySet.add(category.getUidPk());
			}
		}
		dependencyRegistry.addUidDependencies(Category.class, categorySet);
	}

	@Override
	protected int getObjectsQty(final Product domain) {
		return domain.getCategories().size();
	}

	/**
	 * Gets the productCategoryAdapter.
	 * 
	 * @return the productCategoryAdapter
	 * @see ProductCategoryAdapter
	 */
	public ProductCategoryAdapter getProductCategoryAdapter() {
		return productCategoryAdapter;
	}

	/**
	 * Sets the productCategoryAdapter.
	 * 
	 * @param productCategoryAdapter the productCategoryAdapter to set
	 * @see ProductCategoryAdapter
	 */
	public void setProductCategoryAdapter(final ProductCategoryAdapter productCategoryAdapter) {
		this.productCategoryAdapter = productCategoryAdapter;
	}

	/**
	 * Gets the productService.
	 * 
	 * @return the productService
	 */
	public ProductService getProductService() {
		return productService;
	}

	/**
	 * Sets the productService.
	 * 
	 * @param productService the productService to set
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Gets the productLoadTuner.
	 * 
	 * @return the productLoadTuner
	 */
	public ProductLoadTuner getProductLoadTuner() {
		return productLoadTuner;
	}

	/**
	 * Sets the productLoadTuner.
	 * 
	 * @param productLoadTuner the productLoadTuner to set
	 */
	public void setProductLoadTuner(final ProductLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}
}
