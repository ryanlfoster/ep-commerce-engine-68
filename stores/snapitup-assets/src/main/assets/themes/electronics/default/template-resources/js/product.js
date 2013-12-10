// Called to handle return from server with new sku to be displayed
function handleSkuSelection(skuBean, targetId) {
	if (targetId != null) {
		// Update the hidden input for specifying the sku to checkout
		// Note that we hardcode to the first item because there is currently no method for this JS
		// to use more than one cartItem.
		document.getElementById(targetId + ".skuCode").value = skuBean.productSku.skuCode;
        updateJsonBundle(targetId, skuBean);
        // Update the price for calculated bundle item
            recalculatePrice(price);
	} else {
		// Update the inventory
		updateProductInventory(skuBean);

		// go to multi-sku product path
		handleMultiSkuProductSkuSelection(skuBean);

        // update the quantity box
        updateQuantityBoxForSku(skuBean);
	}

	//Reset any error messages
	validateForm(document.getElementById("skuSelectForm"), skuBean, false);
}

// update the quantity box starting from the minimum quantity.
function updateQuantityBoxForSku(skuBean) {

    // Determine the minimum quantity.
    var minQty = 1;
    if (skuBean.priceTiers.length > 0) {
        minQty = skuBean.priceTiers[0].minQty;
    }
    if (minQty < skuBean.minOrderQty) {
    	minQty = skuBean.minOrderQty;
    }
    

    // Determine the maximum quantity to be displayed.
    var maxQty = minQty + 10;
    if (skuBean.inventory && maxQty > skuBean.inventory.availableQuantityInStock) {
        if (minQty > skuBean.inventory.availableQuantityInStock) {
        	maxQty = minQty; // to make sure that will display the minQty.
        } else {
        	maxQty = skuBean.inventory.availableQuantityInStock;
        }
    }
    
    var previousQuantity = document.getElementById("quantitySelect").value;
    updateQuantityBox(minQty, maxQty, previousQuantity);
}

/**
 * minQty Inclusive.
 * maxQty Inclusive.
 */
function updateQuantityBox(minQty, maxQty, previousQuantity)
{
    var quantityBox = document.getElementById("quantitySelect");

    // clear option values
    while (quantityBox.hasChildNodes()) {
        quantityBox.removeChild(quantityBox.lastChild);
    }
    var selectedIndex = -1;
    // populate the option list.
    for (var qty = minQty; qty <= maxQty; qty++) {
        quantityBox[quantityBox.length] = new Option(qty, qty);
        // NOT forget to select the quantity back
        if (previousQuantity == qty) {
        	selectedIndex = quantityBox.length - 1;
        }
    }
    if (selectedIndex >= 0) {
    	quantityBox.selectedIndex = selectedIndex;
    }

}

// Called to handle multi-sku products
function handleMultiSkuProductSkuSelection(newSkuBean) {
	var newSku = newSkuBean.productSku;

	// Update the hidden input for specifying the sku to checkout
	// Note that we hardcode to the first item because there is currently no method for this JS
	// to use more than one cartItem.
	document.getElementById("cartItems[0].skuCode").value = newSku.skuCode;

	// Update the image
	var imageFileName = getProductImageFileName(newSkuBean);

	var newImage = baseUrl() + "/renderImage.image?imageName="
		+ imageFileName + "&width=200&height=200";
	document.getElementById("productImage").src = baseUrl()
		+ "/renderImage.image?imageName=" + imageFileName
		+ "&width=200&height=200";
	;
	document.getElementById("productImageA").href = baseUrl()
		+ "/renderImage.image?imageName=" + imageFileName
		+ "&width=400&height=300";
	;

	// Update the wishlist link
	var wishList = document.getElementById("wishlistA")
	if (wishList != null) {
		wishList.href = baseUrl()
			+ "/add-to-wishlist.ep?skuCode=" + newSku.skuCode;
	}

	// Update the hidden input used to checkout the SKU
	document.getElementById("skuCode").innerHTML = springItemNo() + ": "
			+ newSku.skuCode;

	// Update the price
	// clear out the existing productPrice html
	var priceDiv = document.getElementById("productPrice");
	if(priceDiv == null) {
		return;
	}
	var hasLowestPrice = document.getElementById("lowestPrice") != null;

	while (priceDiv.hasChildNodes()) {
		priceDiv.removeChild(priceDiv.lastChild);
	}
	priceDiv.innerHTML = "";
	if (newSkuBean.priceTierContents.length == 1) {
		// No price tiers
		var lowestPrice = newSkuBean.lowestPrice;
		if (newSkuBean.lowestLessThanList) {
			// on sale
			var saleDiv = document.createElement("div");
			saleDiv.setAttribute("class", "sale-price");
			saleDiv.setAttribute("className", "sale-price"); //for IE
			saleDiv.setAttribute("id", "lowestPrice");
			saleDiv.innerHTML = lowestPrice;
			priceDiv.appendChild(saleDiv);

			var saveDiv = document.createElement("div");
			saveDiv.setAttribute("class", "was-save");
			saveDiv.setAttribute("className", "was-save"); //for IE
			saveDiv.setAttribute("id", "listPrice");
			saveDiv.innerHTML = springProductWas() + " " + newSkuBean.listPrice
					+ ", " + springProductIs() + " " + newSkuBean.dollarSavings;
			priceDiv.appendChild(saveDiv);
		} else {
			var regDiv = document.createElement("div");
			regDiv.setAttribute("class", "reg-price");
			regDiv.setAttribute("className", "reg-price"); //for IE
			regDiv.setAttribute("id", "lowestPrice");
			regDiv.innerHTML = lowestPrice;
			priceDiv.appendChild(regDiv);
		}
	} else {
		var priceTierHtml = "<span class='tier'>";
		var priceTierDiv = document.createElement("div");

		for ( var i = 0; i < newSkuBean.priceTierContents.length; i++) {
			if (i == newSkuBean.priceTierContents.length - 1) {
				priceTierHtml = priceTierHtml
						+ " <div class='top-price-tier'> "
						+ newSkuBean.priceTierContents[i] + "</div>";
			} else {
				priceTierHtml = priceTierHtml + "<div class='regularPrice'>"
						+ newSkuBean.priceTierContents[i] + "</div>";
			}

		}
		priceTierHtml = priceTierHtml + "</span>";
		priceDiv.innerHTML = priceTierHtml;
	}
}

// Called when the user selects a new option value
function handleOptionChange(selectElementClicked) {

	//Disable the submit button while processing
	toggleAddToCart(false);

	//Stores the index of the option just selected
	var selectedOptionIndex = 0;

	//Request a new SKU matching the selected options
	var selectedOptionValues = getSelectedOptionValues(optionSet);

	skuConfigurationService.getAvailableOptionValues(productId, selectedOptionValues,
		function(options) {
			// right now it is one way update (left to right)
			updateOptionValues(options, null, selectedOptionValues, productId);
		}
	);
}

// get selected sku options for multi-sku product.
function getSelectedOptionValues(optionSet) {
	var selectedOptionValues = new Array();

	for ( var i = 0; i < optionSet.length; i++) {
		var currOption = optionSet[i];
		var currSelectElement = document.getElementById(currOption.optionKey);
		selectedOptionValues[selectedOptionValues.length] = currSelectElement.options[currSelectElement.selectedIndex].value;
	}

    return selectedOptionValues;
}

// get selected sku options for multi-sku product within a bundle.
function getSelectedOptionValuesForConstituent(fieldName) {
    var selectedOptionValues = new Array();

	// Every multi option couple has a naming convention.
	// fieldName.select1
	// fieldName.select2
	var i = 1;
	while (document.getElementById(fieldName + ".select" + i) != null) {
		selectedOptionValues[i-1] = document.getElementById(fieldName + ".select" + i++).value;
	}

    return selectedOptionValues;
}

// Called when the user selects a new option from a bundle constituents
function handleConstituentOptionChange(fieldName, productId) {

	//Disable the submit button while processing
	toggleAddToCart(false);

	// Request a new SKU matching the selected options
	var selectedOptionValues = getSelectedOptionValuesForConstituent(fieldName);

    var qty = getQuantity(fieldName);

	skuConfigurationService.getAvailableOptionValues(productId, selectedOptionValues,
		function(options) {
			// right now it is one way update (left to right)
			updateOptionValues(options, fieldName, selectedOptionValues, productId);
		}
	);

	//Enable the submit button after processing
	toggleAddToCart(true);
}

// Updates the option values for given baseTargetId. For products, it updates the Sku option's option key element.
function updateOptionValues(options, baseTargetId, selectedOptions, productId) {
	if (selectedOptions == null) {
		selectedOptions = new Array();
		alert("selectedOptions is null");
	}

    var qty = getQuantity(baseTargetId);
	for (var i = 0; i < options.length; i++) {
		// select the combo box to be updated
		var option = options[i];
		var el;

		if (baseTargetId == null) {
			optionSet = options;
			el = document.getElementById(option.optionKey);

		} else {
			el = document.getElementById(baseTargetId + ".select" + (i + 1));

		}

		// reset the options
		clearComboBox(el);

		//sort the option value
		var sorter = function(a, b) {
			return a.ordering - b.ordering;
		};
		option.optionValues.sort(sorter);

		for (var j = 0; j < option.optionValues.length; j++) {
			// select the sku option value
			var optionValue = option.optionValues[j];
			var htmlOption = createOption(option.optionValues[j]);
			el.options[j] = htmlOption;
		}

		el.selectedIndex = getSelectedOptionIndex(el.options, selectedOptions);
		el.disabled = false;

	}

    // finally we shall update the sku with the new options!
    var selectedOptionValues;
    if (baseTargetId == null) {
        selectedOptionValues = getSelectedOptionValues(options);
    } else {
        selectedOptionValues = getSelectedOptionValuesForConstituent(baseTargetId);
    }
    skuConfigurationService.getSkuWithMatchingOptionValuesAndQuantity(productId, selectedOptionValues, currencyCode, qty,
        function(skuBean) {
            handleSkuSelection(skuBean, baseTargetId);
        }
    );

}

// returns the selected option index
function getSelectedOptionIndex(elementOptions, selectedOptions) {
	for (var i = 0; i < elementOptions.length; i++) {
		for (var j = 0; j < selectedOptions.length; j++) {
			if (elementOptions[i].value == selectedOptions[j]) {
				return i;
			}
		}
	}

	return 0;
}

// Creates an option and returns it
function createOption(optionValue) {
	var localizedPropertyValue = optionValue.localizedPropertiesMap["skuOptionValueDisplayName_"+ localeCode];
	if (!localizedPropertyValue) {
		var localizedPropertyValue = optionValue.localizedPropertiesMap["skuOptionValueDisplayName_"+ defaultLocaleCode];
	}
	return new Option(
					localizedPropertyValue.value,
					optionValue.optionValueKey, false, false
				)
}
// Clears the content of a combo box
function clearComboBox(element) {
	element.options.length = 0;
}

function formatDate(date) {
  	// arrays used for data formatting
  	var m_names = new Array("January", "February", "March",
							"April", "May", "June", "July", "August", "September",
							"October", "November", "December");
	var d_names = new Array("Sun", "Mon", "Tue",
							"Wed", "Thu", "Fri", "Sat");
	var formattedDate = d_names[date.getDay()] + ", " + m_names[date.getMonth()] + " " + date.getDate();
	return formattedDate;
}

// Updates the inventory information display
function updateProductInventory(newSkuBean) {

	var inventory = newSkuBean.inventory;
	var availabilityCode = newSkuBean.availabilityCode;

	var infiniteQuantity = newSkuBean.infiniteQuantity;

	if (inventory != null) {
		availableQuantity = inventory.availableQuantityInStock;
	}

	var expectedReleaseDate = getExpectedReleaseDate();

	var availabilityBoxElement = document.getElementById("availability-box");

	// remove all children. this is needed in order to show restock date only
	// when needed per sku
	while (availabilityBoxElement.hasChildNodes()) {
		availabilityBoxElement.removeChild(availabilityBoxElement.lastChild);
	}

	// create new element for the inventory availability text
	var inventoryTextElement = document.createElement("div");
	inventoryTextElement.setAttribute("id", "inventoryText");
	inventoryTextElement.className = "instock";
	availabilityBoxElement.appendChild(inventoryTextElement);

	// create restock element for representing restock/expected availability
	// dates
	var restockDateElement = document.createElement("div");
	restockDateElement.setAttribute("id", "restockDate");
	restockDateElement.className = "sku";

	// add to cart button
	var addToCartButton = document.getElementById("addToCartSubmit");
	var preOrderButtonName = springPreorder();
	if (!isUpdate()) {
		addToCartButton.value = springAddToCart();
	}

	if (availabilityCode == "IN_STOCK") {
		if (!infiniteQuantity && inventory != null) {
			inventoryTextElement.innerHTML = springInStock() + ", "
					+ availableQuantity + "&nbsp;" + springItemsAvailable();
		} else {
			inventoryTextElement.innerHTML = springInStock();
		}
		addToCartButton.disabled = false;
		addToCartButton.className = "add-to-cart";
		inventoryTextElement.className = "instock";
	} else if (availabilityCode == "OUT_OF_STOCK") {
		inventoryTextElement.innerHTML = springOutOfStock();
		inventoryTextElement.className = "outstock";
		addToCartButton.disabled = true;
		addToCartButton.className = "add-to-cart off";
	} else if (availabilityCode == "OUT_OF_STOCK_WITH_RESTOCK_DATE") {
		inventoryTextElement.innerHTML = springOutOfStock();
		inventoryTextElement.className = "outstock";
		addToCartButton.disabled = true;
		addToCartButton.className = "add-to-cart off";
		if (inventory != null && inventory.restockDate != null) {
			restockDateElement.innerHTML = "(" + springRestockDate() + ": "
					+ formatDate(inventory.restockDate) + ")";
			availabilityBoxElement.appendChild(restockDateElement);
		}
	} else if (availabilityCode == "AVAILABLE_FOR_PREORDER") {
		inventoryTextElement.innerHTML = springAvailableForPreOrder();
		if (expectedReleaseDate != null) {
			restockDateElement.innerHTML = "(" + springPreOrderRestockDate()
					+ ":&nbsp;" + expectedReleaseDate + ")";
			availabilityBoxElement.appendChild(restockDateElement);
		}
		if (!isUpdate()) {
			addToCartButton.value = preOrderButtonName;
		}
		addToCartButton.disabled = false;
		addToCartButton.className = "pre-order";
	} else if (availabilityCode == "AVAILABLE_FOR_BACKORDER") {
		inventoryTextElement.innerHTML = springAvailableForBackOrder();
		inventoryTextElement.className = "instock";
		if (inventory != null && inventory.restockDate != null) {
			restockDateElement.innerHTML = "(" + springBackOrderRestockDate()
					+ ":&nbsp;" + formatDate(inventory.restockDate) + ")";
			availabilityBoxElement.appendChild(restockDateElement);
		} else {
			inventoryTextElement.innerHTML = springWillShipWhenAvailable();
		}
		addToCartButton.disabled = false;
	}

	if (newSkuBean.listPrice == null || !newSkuBean.skuAvailable || !newSkuBean.purchasable) {
		addToCartButton.disabled = true;
		addToCartButton.className = "add-to-cart off";
	}
}

function validateForm(optionForm, skuBean, showErrors) {
    if(optionSet) {
        //Check that all options have been selected
        for ( var i = 0; i < optionSet.length; i++) {
            var currOption = optionSet[i];
            var selectElement = document.getElementById(currOption.optionKey);
            var errorSpan = document
                    .getElementById(currOption.optionKey + "-error");
            if (selectElement.options[selectElement.selectedIndex].text == "-- Select --"
                    && showErrors) {
                errorSpan.innerHTML = "<span class='req'>Please select a "
                        + currOption.optionKey + "</span>";
                return false;
            } else {
                errorSpan.innerHTML = "";
            }
        }
    }
	//Check that there is sufficient stock
	if (!skuBean.infiniteQuantity) {
		var quantitySelectElement = document.getElementById("quantitySelect");

 		var selectedQuantity = 0;//in case if the quantity combo is not visible, like in the GC page, assume that the selected quantity is 1
		if (quantitySelectElement.selectedIndex) {
			selectedQuantity = quantitySelectElement.options[quantitySelectElement.selectedIndex].value;
		}

		if (availableQuantity < selectedQuantity && showErrors) {
			document.getElementById("globalInventoryError").innerHTML = "<br><center><span class='error-msg'>"
					+ springInsufficientStock() + "</span></center><br><br>";
			return false;
		}
	}
	document.getElementById("globalInventoryError").innerHTML = "";
	return true;
}

// fieldName: cartItems[0].constituents[0].constituents[0]
// it's needed to remove the prefix "cartItems[0].", then we can look up it in bundle.
function getJsonPath(fieldName){
    var prefix = "cartItems[0].";
    var x = fieldName.indexOf(prefix);
    var path = fieldName;
    if(x>=0){
        path = fieldName.substring(prefix.length);
    }
    return path;
}

// get the quantity of the current bundle item,
// multiply it with the quantity of top level bundle.
// TODO: shall the quantiaty of top bundle also be included within bundle object?
function getQuantity(fieldName){
    var quantity = 1;
    // if the bundle is available, check the item quantity in bundle.
    if( (typeof(bundle)!="undefined")
        && bundle.calculatedBundle
        && (fieldName!=null) ){
        var path = getJsonPath(fieldName);
        var bundle_item = findJSONBundleElement(bundle, path);
        quantity = bundle_item.quantity;
    }
    if(document.getElementById("quantitySelect")){
        quantity = quantity * document.getElementById("quantitySelect").value;
    }
    return quantity;
}

// update the item price in bundle, if it exists
function updateJsonBundle(fieldName, skuBean){
    if( (typeof(bundle)!="undefined")
        && (fieldName!=null) ){
        var path = getJsonPath(fieldName);
        var bundle_item = findJSONBundleElement(bundle, path);

        // update the skuCode
        bundle_item.skuCode = skuBean.productSku.skuCode;

        if (!skuBean.lowestPrice) {
            // clean all price information
            bundle_item.price = 0;
            bundle_item.priceTiers = [];
            bundle_item.recurringPrice = 0;
            bundle_item.recurringPriceTiers = [];
            bundle_item.paymentSchedule = "";

            return;
        }

		// TODO This part shall be refactored when removing client calculation.
        // retrieve price value from the price string
        // "$10.23 per month" => 10.23
        var re = new RegExp(/.*(\d+\.\d+)(.*)/);
        var m = re.exec(skuBean.lowestPrice);

        if (m != null) {
            // m[2] is payment schedule
            // if there is no payment schedule, set the normal price
            if (m[2].length == 0) {
                bundle_item.price = m[1];
                bundle_item.priceTiers = skuBean.priceTiers;
                // clean recurring price information
                bundle_item.recurringPrice = 0;
                bundle_item.recurringPriceTiers = [];
                bundle_item.paymentSchedule = "";
            } else {
                // it's a recurring price so set the recurring price
                bundle_item.recurringPrice = m[1];
                bundle_item.paymentSchedule = m[2];
                // clean normal price
                bundle_item.price = 0;
                bundle_item.priceTiers = [];
            }
        }

    }
}

// It is more appropriate to put this function in product.js.
function toggleAddToCart(enabled) {
	if (enabled && !addToCartDisabled) {
		document.getElementById("addToCartSubmit").className = "add-to-cart";
		document.getElementById("addToCartSubmit").disabled = false;
	} else {
		document.getElementById("addToCartSubmit").className = "add-to-cart off";
		document.getElementById("addToCartSubmit").disabled = true;
	}
}
