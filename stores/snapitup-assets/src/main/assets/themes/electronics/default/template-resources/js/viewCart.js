/**************************************************
 * Cart Item quantity change functions.
 */
	function updateCartItemQuantity(cartItemIndex) {
	  document.shoppingCart.cartAction.value = "updateCartItemQuantity";
	  document.shoppingCart.updateCartItemIndex.value = cartItemIndex;
      document.shoppingCart.submit();
    }

/**************************************************
 * Promotion/gift certificate code functions.
 */

	function applyCode() {
	  document.shoppingCart.cartAction.value = "applyCode";
      document.shoppingCart.submit();
    }

	function removePromoCode(promoCode) {
	  var shoppingCartForm = document.shoppingCart;
	  shoppingCartForm.cartAction.value = "removePromoCode";
	  shoppingCartForm.promoCodeToDelete.value = promoCode;
      shoppingCartForm.submit();
    }

/**************************************************
 * Shipping and tax estimation functions.
 */

function onShippingCountryChange(newSelectedCountryCode) {
    document.getElementById("subCountry_"+curSelectedShippingCountry).style.display="none";
    curSelectedShippingCountry = newSelectedCountryCode;
    document.getElementById("subCountry_"+newSelectedCountryCode).style.display="block";
}

function getCartItemQty() {
	var cartItemQtyArray = new Array();
	var index = 0;
	var cartItemId = "cartItems[INDEX].quantity";
	var curCartItemNode = document.getElementById(cartItemId.replace("INDEX", index));
	while (curCartItemNode != null) {
		cartItemQtyArray[cartItemQtyArray.length] = curCartItemNode.value;
		index ++;
		curCartItemNode = document.getElementById(cartItemId.replace("INDEX", index));
	}
	return cartItemQtyArray;
}

function estimateShippingAndTaxes() {
	var cartItemQtyArray = getCartItemQty();
    var countryCode=document.getElementById("country").value;
    var subCountryCode="";
    if (countryCode) {
  	subCountryCode = document.getElementById("subCountry_"+countryCode).value;
    }
    var zipOrPostalCode = document.getElementById("zipOrPostalCode").value;
    if (countryCode) {
    	DWREngine.beginBatch();
		shoppingCartAjaxController.estimateShippingAndTaxes(countryCode, subCountryCode, zipOrPostalCode,cartItemQtyArray, estimateShippingAndTaxesCallBack);
		shoppingCartAjaxController.getEstimateAddressStr(updateEstimateAddress);
		shoppingCartAjaxController.getCartItemPricesFormattedForLocale(updateCartItemPrice)
		DWREngine.endBatch({verb:"GET", ordered:true});
    } else {
    	alert(specifyShippingStr);
    }
}

function estimateShippingAndTaxesCallBack(shoppingCart) {
    if (shoppingCart.selectedShippingServiceLevel) {
    	selectedShippingServiceLevelId = shoppingCart.selectedShippingServiceLevel.uidPk;
    	if (shoppingCart.shippingServiceLevelList.length > 0) {
        	document.getElementById("calculate-shipping").style.display="none";
        	document.getElementById("shipping-rates").style.display="block";
       	 	showDeliveryOptions(shoppingCart);
        	updateCartSummary(shoppingCart);
    	}
    } else {
    	alert(noDeliveryOptionStr);
    }
}

function showDeliveryOptions(shoppingCart) {
     	var shippingServiceLevels = shoppingCart.shippingServiceLevelList;
		var shippingOptionsTable = document.getElementById("shippingOptionsTableBody");
        while(shippingOptionsTable.hasChildNodes()){ shippingOptionsTable.removeChild(shippingOptionsTable.firstChild); }
        if (shippingServiceLevels && shippingServiceLevels.length > 0) {
            document.getElementById("shippingOptionsTable").style.display="";
            document.getElementById("estimatenoservicealert").style.display="none";

            var namePropertyKey = "shippingServiceLevelDisplayName_" + localeStr;
            var isFirst = true;
            var costTD = '';
            for (var i=0; i < shippingServiceLevels.length; i++) {
            	var shippingServiceLevel = shippingServiceLevels[i];
                var checkedStr = "";
                if (isFirst) {
	                checkedStr = "checked=\"checked\"";
                   // isFirst = false;
                } else if (selectedShippingServiceLevelId > 0) {
                    if (selectedShippingServiceLevelId == shippingServiceLevel.uidPk) {
                        checkedStr = "checked=\"checked\"";
                    }
                } 
                var shippingServiceLevelDisplayName = shippingServiceLevel.localizedProperties.localizedPropertiesMap[namePropertyKey];
		        
	        	isFirst = false;
            	var newRow = document.createElement("tr");
            	var radioNode, radioTD = document.createElement("td");
            	try{
            		radioNode = document.createElement("<input type=\"radio\" onclick=\"onShippingServiceLevelSelect(this);\" " + checkedStr
            	                + "name=\"selectedShippingServiceLevel\">");
            	} catch (err){
            		radioNode = document.createElement("input");
            		radioNode.type="radio";
            		radioNode.name="selectedShippingServiceLevel";
            		radioNode.onclick=function(e){
                          shoppingCartAjaxController.calculateForSelectedShippingServiceLevel(e.target.id, updateCartSummary);
            				}
            		if (checkedStr.length > 0)
                		    radioNode.checked = true;
            	}
            	radioNode.id=shippingServiceLevel.uidPk;
		        radioNode.value=shippingServiceLevel.uidPk;
		        radioTD.appendChild(radioNode);
		        newRow.appendChild(radioTD);

		        var nameTD = document.createElement("td");
	       
		        if (shippingServiceLevelDisplayName) {
	        		nameTD.appendChild(document.createTextNode(shippingServiceLevelDisplayName.value));
        		} else {
        			nameTD.appendChild(document.createTextNode(namePropertyKey));
        		}
	        	
	         
	        	nameTD.className='type';
	        	newRow.appendChild(nameTD);

	        	costTD = document.createElement("td");
	        	costTD.className='rate';
	        	costTD.appendChild(document.createTextNode(''));
	        	costTD.setAttribute('id',  'td' +i);
            
	        	formatMoney(shippingServiceLevel.shippingCost, shoppingCart.locale, 'td'+i);
            
	        	newRow.appendChild(costTD);
	        	shippingOptionsTable.appendChild(newRow);
		        
            }//end for loop
        } else {
            document.getElementById("shippingOptionsTable").style.display="none";
            document.getElementById("estimatenoservicealert").style.display="block";
        }
}


function updateEstimateAddress(addressStr) {
        var estimationAddress, estimationAddressNode = document.getElementById("estimationAddressNode");
        estimationAddressNode.innerHTML = addressStr;
}

function updateCartItemPrice(cartItemPrices) {
	var cartItemPriceId = "cartItems[INDEX].price";
	for (var i = 0; i < cartItemPrices.length; i++) {
		var priceNode = document.getElementById(cartItemPriceId.replace("INDEX", i));
		if (priceNode) {
			document.getElementById(cartItemPriceId.replace("INDEX", i)).innerHTML = cartItemPrices[i];
		}
	}
}

function updateCartSummary (shoppingCart) {
	if (shoppingCart.inclusiveTaxCalculationInUse == false
		&& shoppingCart.subtotalDiscountMoney != null && shoppingCart.subtotalDiscountMoney.amount > 0) {
	    document.getElementById("promotion-exclusive").style.display="";

	    formatMoney(shoppingCart.subtotalDiscountMoney, shoppingCart.locale, "exclusive-discount-value");
	} else {
	    document.getElementById("promotion-exclusive").style.display="none";
	}

	formatMoney(shoppingCart.subtotalMoney, shoppingCart.locale, "subTotalValue");
	
	document.getElementById("shipping").style.display="";

	formatMoney(shoppingCart.shippingCost, shoppingCart.locale, "cartShippingCostValue");
	
	var cartSummaryTable = document.getElementById("cart-summary-table");
   	var rows = cartSummaryTable.getElementsByTagName("tr");
   	var taxRows = new Array();
   	for (var i = 0; i < rows.length; i++){
     	    if(rows[i].id && rows[i].id.match(/tax\d+/)) {
      		rows[i].parentNode.deleteRow(i);
      		i--;
     	    }
     	}
		var hasTax = false;
		var naTaxNode = document.getElementById("tax-na");
		var count = 1;
		if (shoppingCart.localizedTaxMap) {
	    for (var taxCategoryName in shoppingCart.localizedTaxMap) {
	    	hasTax = true;
		    var newRow = cartSummaryTable.tBodies[0].insertRow(naTaxNode.sectionRowIndex);
		    newRow.className = "tax";
		    newRow.id = "tax" + count;
		    count++;

		    var tcTD = newRow.insertCell(0);
	    	    tcTD.setAttribute("class", "title");
	    	    tcTD.appendChild(document.createTextNode(taxCategoryName + ":"));

	    	    var valueTD = newRow.insertCell(1);
	    	    valueTD.setAttribute("class", "value");
	    	    valueTD.setAttribute("id", taxCategoryName +'id');
	    	    valueTD.appendChild(document.createTextNode(''));
	    	    formatMoney(shoppingCart.localizedTaxMap[taxCategoryName], shoppingCart.locale, taxCategoryName +'id');
	    }
	}
	if (hasTax) {
	    document.getElementById("tax-na").style.display="none";
	} else {
	    document.getElementById("tax-na").style.display="";
	}

	if (shoppingCart.inclusiveTaxCalculationInUse == true
		&& shoppingCart.subtotalDiscountMoney != null && shoppingCart.subtotalDiscountMoney.amount > 0)  {
	    document.getElementById("promotion-inclusive").style.display="";
	    formatMoney(shoppingCart.subtotalDiscountMoney, shoppingCart.locale, "inclusive-discount-value");
		
	} else {
	    document.getElementById("promotion-inclusive").style.display="none";
	}

	var giftCertificateRedeemDiv=document.getElementById("gift-certificate-value");
	if (giftCertificateRedeemDiv) {
		formatMoney(shoppingCart.giftCertificateDiscountMoney, shoppingCart.locale, "gift-certificate-value");
	}

	formatMoney(shoppingCart.beforeTaxTotal, shoppingCart.locale, "totalBeforeTaxValue");
	formatMoney(shoppingCart.totalMoney, shoppingCart.locale, "cartTotalValue");

}

function onShippingServiceLevelSelect(selectedRadionObj) {
    shoppingCartAjaxController.calculateForSelectedShippingServiceLevel(selectedRadionObj.value, updateCartSummary);
    selectedRadionObj.checked =true;
}

function changeEstimationAddress() {
	shoppingCartAjaxController.changeEstimationAddress(changeEstimationAddressCallBack);
}

function changeEstimationAddressCallBack(shoppingCart) {
	document.getElementById("calculate-shipping").style.display="block";
        document.getElementById("shipping-rates").style.display="none";
        updateCartSummary(shoppingCart);
}

/**
 * passing extra data to dwr callbacks explained ...
 * http://directwebremoting.org/dwr/documentation/browser/extra-data.html#extraData3
 * 
 */
function formatMoney(money, locale, elementId) {
	 moneyFormatter.format(money, true, locale,
    		createMoneyFormatterCallBack(elementId));
}

/**
 * working with JavaScript closures explained ...
 * http://lmgtfy.com/?q=working+with+javascript+closures
 */
function createMoneyFormatterCallBack (elementId) {
	return { callback:function(data) {
		moneyFormatterCallBack(data, elementId);
	}}; 
}

function moneyFormatterCallBack(dataFromServer, elementId) {
	document.getElementById(elementId).innerHTML = dataFromServer;
}
