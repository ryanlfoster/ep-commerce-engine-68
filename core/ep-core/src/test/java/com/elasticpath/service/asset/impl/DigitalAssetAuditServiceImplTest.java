package com.elasticpath.service.asset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.DigitalAssetAudit;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.DigitalAssetAuditImpl;
import com.elasticpath.domain.catalog.impl.DigitalAssetImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.ElectronicOrderShipmentImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test <code>DigitalAssetAuditServiceImpl</code>.
 */
public class DigitalAssetAuditServiceImplTest extends AbstractEPServiceTestCase {

	private static final long PRODUCT_SKU_UID = 1111L;
	private static final long DIGITAL_ASSET_UID = 2222L;
	private static final long ORDER_SKU_UID = 3333L;
	private static final long DIGITAL_ASSET_AUDIT_UID = 4444L;
	private static final int EXPIRY_DAYS = 3;
	private static final int MAX_TIMES = 3;
	private static final long CUSTOMER_UID = 9122L;


	private DigitalAssetAuditServiceImpl digitalAssetAuditServiceImpl;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.digitalAssetAuditServiceImpl = new DigitalAssetAuditServiceImpl();
		digitalAssetAuditServiceImpl.setPersistenceEngine(getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.DigitalAssetAuditServiceImpl.add(DigitalAssetAudit)'.
	 */
	@Test
	public void testAdd() {
		final DigitalAssetAudit digitalAssetAudit = new DigitalAssetAuditImpl();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).save(with(same(digitalAssetAudit)));
			}
		});
		digitalAssetAuditServiceImpl.add(digitalAssetAudit);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.DigitalAssetAuditServiceImpl.get(long)'.
	 */
	@Test
	public void testGet() {
		stubGetBean(ContextIdNames.DIGITAL_ASSET_AUDIT, DigitalAssetAuditImpl.class);

		final long uid = 1234L;
		final DigitalAssetAudit digitalAssetAudit = new DigitalAssetAuditImpl();
		digitalAssetAudit.setUidPk(uid);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).get(DigitalAssetAuditImpl.class, uid);
				will(returnValue(digitalAssetAudit));
			}
		});
		assertSame(digitalAssetAudit, digitalAssetAuditServiceImpl.get(uid));
		assertSame(digitalAssetAudit, digitalAssetAuditServiceImpl.getObject(uid));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.DigitalAssetAuditServiceImpl.saveOrUpdate(DigitalAssetAudit)'.
	 */
	@Test
	public void testUpdate() {
		final DigitalAssetAudit digitalAssetAudit = new DigitalAssetAuditImpl();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).update(digitalAssetAudit);
				will(returnValue(null));
			}
		});
		digitalAssetAuditServiceImpl.update(digitalAssetAudit);

	}

	/**
	 * Test method for 'com.elasticpath.service.impl.DigitalAssetAuditServiceImpl.remove(DigitalAssetAudit)'.
	 */
	@Test
	public void testRemove() {
		final DigitalAssetAudit digitalAssetAudit = new DigitalAssetAuditImpl();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).delete(with(same(digitalAssetAudit)));
			}
		});
		digitalAssetAuditServiceImpl.remove(digitalAssetAudit);
	}

	/**
	 * Test method for 'com.elasticpath.service.DigitalAssetAuditServiceImpl.list()'.
	 */
	@Test
	public void testList() {
		final List<DigitalAssetAudit> digitalAssetAudits = new ArrayList<DigitalAssetAudit>();
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("DIGITAL_ASSET_AUDIT_SELECT_ALL"), with(any(Object[].class)));
				will(returnValue(digitalAssetAudits));
			}
		});
		assertSame(digitalAssetAudits, digitalAssetAuditServiceImpl.list());
	}

	/**
	 * Test method for 'com.elasticpath.service.DigitalAssetAuditServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		stubGetBean(ContextIdNames.DIGITAL_ASSET_AUDIT, DigitalAssetAuditImpl.class);

		final long uid = 1234L;
		final DigitalAssetAudit digitalAssetAudit = new DigitalAssetAuditImpl();
		digitalAssetAudit.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).load(DigitalAssetAuditImpl.class, uid);
				will(returnValue(digitalAssetAudit));
			}
		});
		final DigitalAssetAudit loadedDigitalAssetAudit = digitalAssetAuditServiceImpl.load(uid);
		assertSame(digitalAssetAudit, loadedDigitalAssetAudit);
	}

	/**
	 * Test method for 'com.elasticpath.service.DigitalAssetAuditServiceImpl.isValidDADownloadRequest(long, long)'.
	 */
	@Test
	public void testIsValidDADownloadRequest() {

		DigitalAssetAuditServiceImpl daAuditServiceImpl = new DigitalAssetAuditServiceImpl() {

			@Override
			public OrderSku getOrderSku(final long orderSkuUid) {

				DigitalAsset digitalAsset = new DigitalAssetImpl();
				digitalAsset.setUidPk(DIGITAL_ASSET_UID);
				digitalAsset.setUidPk(DIGITAL_ASSET_UID);
				digitalAsset.setExpiryDays(EXPIRY_DAYS);
				digitalAsset.setFileName("abc.txt");
				digitalAsset.setMaxDownloadTimes(MAX_TIMES);

				ProductSku productSku = new ProductSkuImpl();
				productSku.setUidPk(PRODUCT_SKU_UID);

				productSku.setDigitalAsset(digitalAsset);

				OrderSku orderSku = new OrderSkuImpl();
				orderSku.setUidPk(ORDER_SKU_UID);
				orderSku.setProductSku(productSku);
				orderSku.setCreatedDate(new Date());
				orderSku.setDigitalAsset(digitalAsset);

				OrderShipment shipment = new ElectronicOrderShipmentImpl() {
					private static final long serialVersionUID = -9136002110390360138L;

					@Override
					protected void recalculate() {
						// override to avoid recalculation
					}
				};
				shipment.addShipmentOrderSku(orderSku);
				Order order = new OrderImpl();
				order.setUidPk(2);
				order.addShipment(shipment);
				Customer customer = new CustomerImpl();
				customer.setUidPk(CUSTOMER_UID);
				order.setCustomer(customer);
				return orderSku;
			}

			@Override
			protected List<DigitalAssetAudit> findDAAuditListByDAOrderSku(final long orderSkuUid, final long digitalAssetUid) {

				ArrayList<DigitalAssetAudit> digitalAssetAuditList = new ArrayList<DigitalAssetAudit>();
				DigitalAssetAudit digitalAssetAudit = new DigitalAssetAuditImpl();
				digitalAssetAudit.setUidPk(DIGITAL_ASSET_AUDIT_UID);
				digitalAssetAuditList.add(digitalAssetAudit);
				return digitalAssetAuditList;
			}

		};

		daAuditServiceImpl.setPersistenceEngine(getPersistenceEngine());

		int errorType = daAuditServiceImpl.isValidDADownloadRequest(DIGITAL_ASSET_UID, ORDER_SKU_UID, CUSTOMER_UID);

		assertEquals(errorType, 0);
	}


	/**
	 * Test method for 'com.elasticpath.service.DigitalAssetAuditServiceImpl.addDigitalAssetAudit(String, long, long)'.
	 */
	@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
	@Test
	public void testAddDigitalAssetAudit() {

		DigitalAsset digitalAsset = new DigitalAssetImpl();
		digitalAsset.setUidPk(DIGITAL_ASSET_UID);

		ProductSku productSku = new ProductSkuImpl();
		productSku.setUidPk(PRODUCT_SKU_UID);


		productSku.setDigitalAsset(digitalAsset);

		final OrderSku orderSku = new OrderSkuImpl();
		orderSku.setUidPk(ORDER_SKU_UID);
		orderSku.setProductSku(productSku);

		final ArrayList<OrderSku> orderSkuList = new ArrayList<OrderSku>();
		orderSkuList.add(orderSku);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("ORDER_SKU_SELECT_BY_ID", ORDER_SKU_UID);
				will(returnValue(orderSkuList));
			}
		});

		String ipAddress = "127.0.0.1";

		final DigitalAssetAudit digitalAssetAudit = new DigitalAssetAuditImpl();
		digitalAssetAudit.setUidPk(DIGITAL_ASSET_AUDIT_UID);
		digitalAssetAudit.setDigitalAsset(digitalAsset);
		digitalAssetAudit.setOrderSku(orderSku);
		digitalAssetAudit.setIpAddress(ipAddress);
		stubGetBean(ContextIdNames.DIGITAL_ASSET_AUDIT, digitalAssetAudit);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).save(with(same(digitalAssetAudit)));
			}
		});

		DigitalAssetAudit retDigitalAssetAudit = this.digitalAssetAuditServiceImpl.addDigitalAssetAudit(
				ipAddress, DIGITAL_ASSET_UID, ORDER_SKU_UID);
		assertEquals(DIGITAL_ASSET_AUDIT_UID, retDigitalAssetAudit.getUidPk());
	}


}
