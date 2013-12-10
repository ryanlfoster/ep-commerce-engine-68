package com.elasticpath.service.changeset.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.changeset.ChangeSetDependencyResolver;
import com.elasticpath.service.rules.RuleService;

/**
 *		Resolver for Catalog Promotions. 
 */
public class PromotionChangeSetDependencyResolverImpl implements ChangeSetDependencyResolver {

	private RuleService ruleService;
	
	private ProductService productService;
	private CategoryService categoryService;
	private ProductSkuService productSkuService; 
	private BrandService brandService;
	
	@Override
	public Set<?> getChangeSetDependency(final Object object) {
		
		if (object instanceof Rule) {
			Set<Object> depends = new LinkedHashSet<Object>();
			Rule rule = (Rule) object;
			depends.addAll(getDependentsForRuleSet(rule.getRuleElements()));
			depends.addAll(getDependentsForRuleSet(rule.getConditions()));
			
			return depends;
		}
		return Collections.emptySet();
	}

	

	private Set<Object> getDependentsForRuleSet(final Set< ? extends RuleElement> ruleElements) {
		Set<Object> depends = new LinkedHashSet<Object>();
		for (RuleElement condition : ruleElements) {
			Set<RuleParameter> parameters = condition.getParameters();
			for (RuleParameter parameter : parameters) {
				depends.addAll(getDependentObjects(parameter));
			}
		}
		return depends;
	}
	
	private Set<Object> getDependentObjects(final RuleParameter parameter) {
		Set<Object> objects = new LinkedHashSet<Object>();
		if (parameter.getKey().equals(RuleParameter.PRODUCT_CODE_KEY)) {
			Product prod = getProductService().findByGuid(parameter.getValue());
			objects.add(prod);
		} else if (parameter.getKey().equals(RuleParameter.BRAND_CODE_KEY)) {
			Brand brand = getBrandService().findByCode(parameter.getValue());
			objects.add(brand);
		} else if (parameter.getKey().equals(RuleParameter.CATEGORY_CODE_KEY)) {
			String value = getCategoryCodeFromParameter(parameter);
			Category category = getCategoryService().findByGuid(value);
			objects.add(category);
		} else if (parameter.getKey().equals(RuleParameter.SKU_CODE_KEY)) {
			ProductSku productSku = getProductSkuService().findBySkuCode(parameter.getValue());
			Product product = productSku.getProduct();
			if (product.hasMultipleSkus()) {
				objects.add(productSku);
			} else {
				objects.add(product);
			}
		}

		return objects;
	}
	

	
	private String getCategoryCodeFromParameter(final RuleParameter parameter) {
		String value = parameter.getValue();
		String[] result = value.split("\\|");
		return result[0];
	}

	
	@Override
	public Object getObject(final BusinessObjectDescriptor object, final  Class<?> objectClass) {
		if (Rule.class.isAssignableFrom(objectClass)) {
			return getRuleService().findByRuleCode(object.getObjectIdentifier());
		
		}
		return null;
	}

	private RuleService getRuleService() {
		return ruleService;
	}
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}
	private ProductService getProductService() {
		return productService;
	}
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	private CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	private ProductSkuService getProductSkuService() {
		return productSkuService;
	}

	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

	public BrandService getBrandService() {
		return brandService;
	}

	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
	}
}
