package com.elasticpath.common.pricing.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.assembler.pricing.BaseAmountDtoAssembler;
import com.elasticpath.common.dto.category.ChangeSetObjectsImpl;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test the PriceListService.
 */
public class BaseAmountAllOrNothingUpdateStrategyTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final BaseAmountUpdateStrategyImpl strategy = new BaseAmountUpdateStrategyImpl();
	private BaseAmountDtoAssembler baDtoAssembler = null;
	private final BaseAmountService baService = context.mock(BaseAmountService.class);
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Set up method.
	 */
	@Before
	public void setUp() {
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		strategy.setBaseAmountService(baService);
		strategy.setBeanFactory(beanFactory);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}
	
	/**
	 * Test that the strategy processes the BaseAmountDTO changeset by sending
	 * BaseAmount objects to the BaseAmountService.
	 * @throws Exception on error
	 */
	@Test
	public void testProcessBaseAmountChangeSet() throws Exception {
		final BaseAmount ba1 = getNewBaseAmount();
		final BaseAmount ba2 = getNewBaseAmount();
		final BaseAmount ba3 = getNewBaseAmount();
		final List <BaseAmount> adds = new ArrayList <BaseAmount>();
		adds.add(ba1);
		
		final List <BaseAmount> updates = new ArrayList <BaseAmount>();
		updates.add(ba2);

		final List <BaseAmount> removes = new ArrayList <BaseAmount>();
		removes.add(ba3);
		
		final List <BaseAmountDTO> addsMarker = new ArrayList <BaseAmountDTO>();
		final List <BaseAmountDTO> removesMarker = new ArrayList <BaseAmountDTO>();
		final List <BaseAmountDTO> updatesMarker = new ArrayList <BaseAmountDTO>();
		//Markers are used because we can't mock the assembler impl class.
		baDtoAssembler = new BaseAmountDtoAssembler() {
			@Override
			public List <BaseAmount> assembleDomain(final Collection <BaseAmountDTO> dtos) {
				if (dtos == addsMarker) {  //NOPMD
					return adds;
				}
				if (dtos == updatesMarker) {  //NOPMD
					return updates;
				}
				if (dtos == removesMarker) {  //NOPMD
					return removes;
				}
				return null;
			}
		};
		strategy.setBaseAmountDtoAssembler(baDtoAssembler);

		final ChangeSetObjects<BaseAmountDTO> changeSet = new ChangeSetObjectsImpl<BaseAmountDTO>() {
			private static final long serialVersionUID = 3834142519537237698L;

			/** @return the marker list used for mocking **/
			@Override
			public List <BaseAmountDTO> getAdditionList() {
				return addsMarker;
			}
			/** @return the marker list used for mocking **/
			@Override
			public List <BaseAmountDTO> getUpdateList() {
				return updatesMarker;
			}
			/** @return the marker list used for mocking **/
			@Override
			public List <BaseAmountDTO> getRemovalList() {
				return removesMarker;
			}
		};


		context.checking(new Expectations() {
			{
				allowing(baService).findByGuid(with(ba3.getGuid()));
				will(returnValue(ba3));
				allowing(baService).findByGuid(with(ba2.getGuid()));
				will(returnValue(ba2));
				allowing(beanFactory).getBean(with(ContextIdNames.BASE_AMOUNT_UPDATE_STRATEGY));
				will(returnValue(strategy));
				oneOf(baService).delete(with(ba3));
				oneOf(baService).updateWithoutLoad(with(any(BaseAmount.class)));
				oneOf(baService).add(with(ba1));
			}
		});

		strategy.modifyBaseAmounts(changeSet);
	}
	
	/**
	 * Test that the strategy fails on error.
	 */
	@Test(expected = EpServiceException.class)
	public void testProcessBaseAmountException() {
		final BaseAmount ba1 = getNewBaseAmount();
		final BaseAmount ba2 = getNewBaseAmount();
		final BaseAmount ba3 = getNewBaseAmount();
		final List <BaseAmount> adds = new ArrayList <BaseAmount>();
		adds.add(ba1);
		final List <BaseAmount> updates = new ArrayList <BaseAmount>();
		updates.add(ba2);
		final List <BaseAmount> removes = new ArrayList <BaseAmount>();
		removes.add(ba3);
		
		
		final List <BaseAmountDTO> addsMarker = new ArrayList <BaseAmountDTO>();
		final List <BaseAmountDTO> removesMarker = new ArrayList <BaseAmountDTO>();
		final List <BaseAmountDTO> updatesMarker = new ArrayList <BaseAmountDTO>();

		baDtoAssembler = new BaseAmountDtoAssembler() {
			@Override
			public List <BaseAmount> assembleDomain(final Collection <BaseAmountDTO> dtos) {
				if (dtos == addsMarker) { //NOPMD
					return adds;
				}
				if (dtos == updatesMarker) { //NOPMD
					return updates;
				}
				if (dtos == removesMarker) { //NOPMD
					return removes;
				}
				return null;
			}
		};
		strategy.setBaseAmountDtoAssembler(baDtoAssembler);

		final ChangeSetObjects<BaseAmountDTO> changeSet = new ChangeSetObjectsImpl<BaseAmountDTO>() {
			private static final long serialVersionUID = 2882875236107791080L;

			/** @return the marker list used for mocking **/
			@Override
			public List <BaseAmountDTO> getAdditionList() {
				return addsMarker;
			}
			/** @return the marker list	used for mocking **/
			@Override
			public List <BaseAmountDTO> getUpdateList() {
				return updatesMarker;
			}
			/** @return the marker list used for mocking **/
			@Override
			public List <BaseAmountDTO> getRemovalList() {
				return removesMarker;
			}
		};

		context.checking(new Expectations() {
			{
				allowing(baService).findByGuid(with(ba3.getGuid()));
				will(returnValue(ba3));
				allowing(baService).findByGuid(with(ba2.getGuid()));
				will(returnValue(ba2));
				allowing(beanFactory).getBean(with(ContextIdNames.BASE_AMOUNT_UPDATE_STRATEGY));
				will(returnValue(strategy));
				oneOf(baService).delete(with(ba3));
				oneOf(baService).add(with(ba1));
				will(throwException(new EpServiceException("Passed On")));
			}
		});
		strategy.modifyBaseAmounts(changeSet);
	}
	
	private BaseAmount getNewBaseAmount() {
		long modifier = RandomUtils.nextInt();
		return new BaseAmountImpl("GUID" + modifier, "OBJ_GUID" + modifier, "PROD_TYPE" + modifier, 
				new BigDecimal(1 + modifier), new BigDecimal(2 + modifier), new BigDecimal(1 + modifier), "PLD_GUID" + modifier);
	}
}
