package com.elasticpath.domain.misc;

import java.io.Serializable;
import java.util.Comparator;

import com.elasticpath.domain.rules.CouponUsage;
/**
 * Comparator for ordering CouponUsage objects by coupon code.
 */
public interface CouponUsageByCouponCodeComparator extends Comparator<CouponUsage>, Serializable {

}
