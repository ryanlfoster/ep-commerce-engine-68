var invalidSelections = [];

/**
 * Select the default selections on a bundle element.
 *
 * @param {Object} element
 * @param {Object} levelSelectionRule
 */
function selectDefaults(element, levelSelectionRule) {
	if (levelSelectionRule == 0) {
		levelSelectionRule = element.constituents.length;
	}

	for (var currentElementIndex in element.constituents) {
		var currentElement = element.constituents[currentElementIndex];
		var el = findDomElementFromJSONElement(currentElement);
		el.disabled = false;
        el.checked = false;
		currentElement.selected = false;
		if (levelSelectionRule > 0) {
			currentElement.selected = true;
			el.checked = true;
			levelSelectionRule--;
			if (currentElement.constituents.length > 0) {
				selectDefaults(currentElement, currentElement.selectionRule);
			}
		} else {
			toggleChildSelections(currentElement.path);
		}
	}
}

/**
 * Validate that the current level selections satisfies the selection rule.
 * @param {Object} path
 */
function validateLevel(path){
	var parent = findJSONParent(bundle, path);
	var rule = parent.selectionRule;
	var isValid = true;
	if (rule == 0)
		return;

	for (index in parent.constituents) {
		var currentElement = parent.constituents[index];
		var element = findDomElementFromJSONElement(currentElement);
		if (element.checked) {
			rule--
		}
	}

	if (rule != 0) {
		highlightDomElement(parent.path);
	}
	else {
		returnDomElementToNormal(parent.path);
	}

	var currentNode = document.getElementById("cartItems[0]." + path + ".selected");
	if (!currentNode.checked) {
		returnDomElementToNormal(path);
	}

}

/**
 * Remove item path from list of invaild selections.
 * @param {Object} path
 */
function removeFromInvalidSelections(path) {
	for ( index in invalidSelections ) {
		if (invalidSelections[index] == path) {
			delete invalidSelections[index];
		}
	}
	invalidSelections = mergeArray(invalidSelections);
	toggleAddToCart(invalidSelections.length == 0);
}

function mergeArray(array) {
	var result = new Array();
	var index = 0;
	for (i in array) {
		if (typeof(array[i]) != "undefined") {
			result[index++] = array[i];
		}
	}
	return result;
}

/**
 * Add item path to list of invalid selections.
 * @param {Object} path
 */
function addInvalidSelection(path) {
	var exists = false;
	for ( index in invalidSelections ) {
		if (invalidSelections[index] == path) {
			exists = true;
		}
	}
	if (!exists) {
		invalidSelections.push(path);
	}
	toggleAddToCart(invalidSelections.length == 0);
}


/**
 * Highlight the html dom element.
 * @param {Object} jsonPath
 */
function highlightDomElement(jsonPath) {
	var path;
	if (!jsonPath) {
		path = "cartItems[0].selectionRule";
	}
	else {
		path = 'cartItems[0].' + jsonPath + '.selectionRule';
	}
	var highlight = document.getElementById(path);
	highlight.style.color = "white";
	jq(highlight).effect("highlight", {}, 2000);
	highlight.style.color = "red";
	addInvalidSelection(jsonPath);
}

/**
 * Revert the html dom element highlight.
 * @param {Object} jsonPath
 */
function returnDomElementToNormal(jsonPath) {
	if (!jsonPath) {
		path = "cartItems[0].selectionRule";
	}
	else {
		path = 'cartItems[0].' + jsonPath + '.selectionRule';
	}
	var highlight = document.getElementById(path);
	if (highlight) {
		highlight.style.color = 'black';
	}
	removeFromInvalidSelections(jsonPath);
}

/**
 * Disable all sibling radios and recurse into their children. Select current element.
 * @param {Object} path
 */
function toggleSiblingRadio(path) {
	var parent = findJSONParent(bundle, path);
	for (index in parent.constituents) {
		var sibling = parent.constituents[index];
		var siblingDomElement = findDomElementFromJSONElement(sibling);
		if (sibling.constituents.length > 0) {
			returnDomElementToNormal(sibling.path);
		}
		siblingDomElement.checked = false;
		sibling.selected = false;

		toggleChildSelections(sibling.path);
	}
	selectCurrentRadioDefaults(path);
}

/**
 * Select the defaults of the radio button and enable.
 * @param {Object} path
 */
function selectCurrentRadioDefaults(path) {
	var element = findDomElementForBundlePath(path);
	element.checked = true;
	var node = findJSONBundleElement(bundle, path);
    node.selected = true;
	var selectionRule = (node.selectionRule) ? node.selectionRule : 0;
	selectDefaults(node, selectionRule);
}

/*
 * User clicked checkbox of an item.
 */
function toggleItemCheckbox(path, checkBox) {
	var node = findJSONBundleElement(bundle, path);
    node.selected = checkBox.checked;
	toggleChildSelections(path);
}

function findJSONParent(root, path) {
	var found = null;
	for (index in root.constituents) {
		var currentElement = root.constituents[index];
		if (currentElement.path == path) {
			return root
		}
		if (currentElement.constituents.length > 0) {
			found = findJSONParent(currentElement, path);
			if (found != null) {
				return found;
			}
		}
	}
	return found;
}

function findJSONBundleElement(root, path) {
	var found = null;
	for (index in root.constituents) {
		var currentElement = root.constituents[index];
		if (currentElement.path == path) {
			return currentElement;
		}
		if (currentElement.constituents.length > 0) {
			found = findJSONBundleElement(currentElement, path);
			if (found != null) {
				return found;
			}
		}
	}
	return found;
}

/**
 * Toggle checked status of child elements according to their parent element.
 * Recursively selects defaults when needed or recursively disable.
 * @param {Object} path
 * @param {Object} isUpdate
 */
function toggleChildSelections(path, isUpdate) {
	var element = findJSONBundleElement(bundle, path);
	var domElement = findDomElementFromJSONElement(element);

	for (index in element.constituents) {
		var currentElement = element.constituents[index];
		var el = findDomElementFromJSONElement(currentElement);
		if (element.selected == false) {
			el.checked = false;
			currentElement.selected = false;
			el.disabled = true;
		}
		if (currentElement.constituents.length > 0) {
			returnDomElementToNormal(currentElement.path);
			toggleChildSelections(currentElement.path, isUpdate);
		}
	}
	if (domElement.checked == true && isUpdate != true) {
		selectDefaults(element, element.selectionRule);
		return;
	}
}

function toggleOnUpdate(jsonParent) {
	for (index in jsonParent.constituents) {
		var jsonElement = jsonParent.constituents[index];
		var domElement = findDomElementFromJSONElement(jsonElement);

		toggleChildSelections(jsonElement.path, true);

	}
}

function findDomElementForBundlePath(path) {
	return document.getElementById("cartItems[0]." + path + ".selected");
}

function findDomElementFromJSONElement(jsonElement) {
	return findDomElementForBundlePath(jsonElement.path);
}




function accumulatePriceAdjustments(root) {
    var value = 0;
    for (index in root.constituents) {
        if (root.constituents[index].selected && root.constituents[index].priceAdjustment)
            value += root.constituents[index].priceAdjustment
        if (root.constituents[index].constituents.length > 0)
            value += accumulatePriceAdjustments(root.constituents[index])
    }
    return value;
}

function calculatePriceForAssignedPriceBundle(price) {
	var totalAdjustments = accumulatePriceAdjustments(bundle);
    //Check if there are price tiers, which determines whether to use 'lowestPrice' or 'tier-tierindex'
    if (document.getElementById("lowestPrice"))
        document.getElementById("lowestPrice").innerHTML = currencySymbol + (totalAdjustments + price).toFixed(2);
    else if (priceTiers.length > 0) {
    	for (var index = 0; index < priceTiers.length; index++) {
    		//get appropriate price tier element
    		var tierDomElement = document.getElementById("tier-price-" + index);
    		if (tierDomElement) {
    			tierDomElement.innerHTML = currencySymbol + (totalAdjustments + priceTiers[index]).toFixed(2);
    		}
    	}
    }

}

/*
Recalculate price is called when the product details page loads, and when one of the bundle constituent items toggle.

It will delegate to either the assigned price bundle calculation mechanism, or the calculated bundle mechanism, depending on the value of calculatedBundle
at the top level of the JSON bundle object.

*/
function recalculatePrice(price) {
	if (bundle.calculatedBundle) {
		recalculatePriceOnServer();
	} else {
		calculatePriceForAssignedPriceBundle(price);
	}
}

/**
Make a call back to the server via dwr to update the json bundle object, and then update the UI based on the contents of the callback.
the global variable mostRecentRecalcAjaxCallId remembers the most recent ajax call.
It was reported, the result of most recent call was overriden by the previous call, 
because the most recent call was simpler & faster than the previous one, typically when you deselct items.    
Details can be seen in JIRA LR-1179.
*/
var mostRecentRecalcAjaxCallId = 0;
function recalculatePriceOnServer() {
	/*var errorDiv = document.getElementById('updatePriceServiceCallError');
	if (errorDiv) {
		errorDiv.style.display = 'none';
	}*/
	// ask server to update prices:
	jsonBundleService.updateJsonBundle(bundle,
			{
            // each time we make an ajax call, we give it an id.
            callId: ++mostRecentRecalcAjaxCallId,
            callback: function(updatedBundle) {
                // when the ajax call returns, it shall check whether this is the most recent call.
                // if not, it shall do NOTHING.
                if (this.callId < mostRecentRecalcAjaxCallId) {
                    return;
                }
				bundle = updatedBundle;
				// update the UI with the updated bundle
				updateUIfromBundleCallback();
			},
			errorHandler: function (message, ex) {
				var errorDiv = document.getElementById('updatePriceServiceCallError');
				if (errorDiv) {
					errorDiv.style.display = 'inline';
				}
			}
			});
}

/*
Calculate the bundle price for the given bundle quantity.

This function will call itself recursively until it encounters a selected constituent.

Find the tiered price for the selected bundle constituents.
Multiply this by the quantity of that constituent item.

Once the tiered price is found, if the adjustPrices argument is true the element on the page will be adjusted.

Add any positive or negative adjustments.

Finally return the calculated value.

*/
function calculatePriceForCalculatedBundleQuantity(root, bundleQuantity, adjustPrices) {


	var value = 0;
		// iterate through the bundle prices, summing them up
		for (index in root.constituents) {

			// first add the price, multiplied by the quantity

			// if there is a price, which indicates that this constituent item is a sku, find the new price //root.constituents[index].price
			// if the constituent is an item sku, it will have properties calculatedBundle False and calculatedBundleItem True


			if (root.constituents[index].calculatedBundle == false && root.constituents[index].calculatedBundleItem == true) {

				// the correct tiered price to find should be the result of the bundleQuantity by the constituent quantity

				var tierQuantityToFind = bundleQuantity * root.constituents[index].quantity;

				var itemPrice = findTieredPrice(root, index, tierQuantityToFind);

				// change the displayed price for this constituent item if adjustPrices is true and item has price
				if (adjustPrices == 'true' && root.constituents[index].priceTiers.length > 0 ) {

					var currentElement = root.constituents[index];

					document.getElementById("cartItems[0]." + currentElement.path + ".price").innerHTML = currencySymbol + itemPrice.toFixed(2);
				}


				if (root.constituents[index].selected) {

				// update the value
				value += itemPrice * root.constituents[index].quantity;

				}

			}

			// then add the adjustment if applicable
			if (root.constituents[index].selected && root.constituents[index].priceAdjustment) {

				value += root.constituents[index].priceAdjustment;
			}

			// make a recursive call for nested bundles
			if (root.constituents[index].constituents.length > 0) {

				value += calculatePriceForCalculatedBundleQuantity(root.constituents[index], bundleQuantity * root.constituents[index].quantity, adjustPrices);
			}


		}

	return value;
}

/*
Find the tiered price for the given root constituent object and the given quantity.

The JSON bundle object has included all the price tiers for each constituent object.

If the evaluation of a given price tier is undefined, then call the function recursively to get the previous price tier,
as long as the quantity is greater than zero.

If the price tier is defined, it will simply be returned.


*/
function findTieredPrice(root, index, quantity) {

	return findPriceFromPriceTiers(root.constituents[index].priceTiers, quantity);

}

function findPriceFromPriceTiers(priceTiers, quantity) {
	for (var priceTierIndex = priceTiers.length - 1; priceTierIndex >= 0; priceTierIndex--) {
		var minQty = priceTiers[priceTierIndex].minQty;
		if (quantity >= minQty) {
			return priceTiers[priceTierIndex].price;
		}
	}
	return 0.0;
}

/*
This function is called when the quantity is changed for a calculated bundle.

The argument object is the object that represents the quantity dropdown index and value.

Determine the change to all selected and non selected consituent item display prices, and change these document objects.

Calculate the unit bundle price for the selected constituent items for every available price tier.

for example, for bundle constituents A, B and C, where:

- " represents no change in price
- only A B or C may be selected , i.e. there is only 1 possible selection for this bundle

		A		B		C
(qty)
1		100		100		100
2		75		"		"
5		50		"		40
10		"		"		10

1) changing the bundle quantity to 5 would represent a change in display for A and C
2) if item B was the selection, regardless of quantity selection the bundle price would not change
3) all possible price tier selections should be displayed, in the following manner :

(qty)
1
2-4
5-9
10

where the price displayed to the right of that tier quantity will be the price for that selection.

So if a quantity of 5 had been selected, and C was selected, the tiered price to display will be 40.


*/
function handleQuantityChangeForCalculatedBundle(object) {
    // if current product is not a calculated bundle, do nothing!
    if (!(bundle && bundle.calculatedBundle)) {
        return;
    }

    bundle.quantity = object.value;
    // ask server to update prices:
    recalculatePriceOnServer();

}

/*
Update all sku items price using the updated bundle object.

Update the constituent item prices, then update the bundle price.

*/
function updateUIfromBundleCallback() {

	// passing in the global bundle instance as this is required by the function which calls itself recursively
	updateItemPrices(bundle);

    var bundleAvailable = isBundleAvailable(bundle);

    updateBundleTierPriceDisplay(bundleAvailable);

    updateQuantityBoxForCalculatedBundle(bundle);

    // added check for invlid selections, it was done by validateLevel() which is triggered by checkBox.onclick
    toggleAddToCart(bundleAvailable && (invalidSelections.length == 0));
}

// check whether each selected item is available.
// An item is available is only if its priceTiers or recurringPriceTiers is NOT empty.
function isBundleAvailable(bundle) {
    for (var i = 0; i < bundle.constituents.length; i++) {
        var currentItem = bundle.constituents[i];
        if (currentItem.calculatedBundleItem
            && !currentItem.calculatedBundle
            && currentItem.selected) {

            var hasNormalPriceTier = (currentItem.priceTiers && currentItem.priceTiers.length > 0);
            var hasRecurringPriceTier = (currentItem.recurringPriceTiers && currentItem.recurringPriceTiers.length > 0);

            if (!hasNormalPriceTier && !hasRecurringPriceTier){
                return false;
            }

        } else if (currentItem.calculatedBundleItem
            && currentItem.calculatedBundle
            && currentItem.selected) {
            // recursively check selected nested bundle
            if (!isBundleAvailable(currentItem)) {
                return false;
            }
        }
    }

    return true;
}

/**
 * Traverse the bundle tree, find out the maximum lowest quantity from the selected items
 */
function updateQuantityBoxForCalculatedBundle(bundle) {
    var firstTierQuantities = new Array();
    // populate all first tier minQty's
    populateFirstTierQuantities(bundle, firstTierQuantities);

    // get the maximum first tier minQty
    firstTierQuantities.sort();
    var lowestQuantityOfBundle = firstTierQuantities[firstTierQuantities.length - 1];

    // update the quantity box
    updateQuantityBox(lowestQuantityOfBundle, Math.max(lowestQuantityOfBundle, bundle.quantity) + 10, bundle.quantity);
}

/**
 * Traverse the bundle tree, populate all first tier minQty's
 */
function populateFirstTierQuantities(bundle, firstTierQuantities) {
    for (var i =0; i < bundle.constituents.length; i++) {
        var currentItem = bundle.constituents[i];
        if (currentItem.calculatedBundleItem
            && !currentItem.calculatedBundle
            && currentItem.selected) {

            if (currentItem.paymentSchedule.length > 0
                && currentItem.recurringPriceTiers.length > 0){
                firstTierQuantities.push(currentItem.recurringPriceTiers[0].minQty);
            } else if (currentItem.priceTiers.length > 0) {
                firstTierQuantities.push(currentItem.priceTiers[0].minQty);
            }
        } else if (currentItem.calculatedBundle) {

            populateFirstTierQuantities(currentItem, firstTierQuantities);

        }
    }
}

/*
 * Convenience method to get the bundleName element by the id that is the json object path.
 */
function getBundleNameElementId(jsonPath) {
    return "cartItems[0]." + jsonPath + ".bundleName";
}

/*
 * Convenience method to get the price element by the id that is the json object path.
 */
function getPriceElementId(jsonPath) {
    return "cartItems[0]." + jsonPath + ".price";
}

function getAdjustmentElementId(jsonPath) {
    return "cartItems[0]." + jsonPath + ".adjustment";
}

function getPriceFrequencyElementId(jsonPath) {
    return "cartItems[0]." + jsonPath + ".price-frequency";
}

function getAdjustmentFrequencyElementId(jsonPath) {
    return "cartItems[0]." + jsonPath + ".adjustment-frequency";
}


/*
 * Calls itself recursively to update the item price based on the value of the price element at that level of the json bundle item.
 */
function updateItemPrices(bundleItem){
    // update UI for bundle items
    var priceElemId = getPriceElementId(bundleItem.path);
    var adjustmentElemId = getAdjustmentElementId(bundleItem.path);
    var priceFrequencyElemId = getPriceFrequencyElementId(bundleItem.path);
    var adjustmentFrequencyElemId = getAdjustmentFrequencyElementId(bundleItem.path);

    // the condition "bundleItem.priceTiers.length > 0" is to not display "$0.00" for non priced items.
    if (bundleItem.calculatedBundle == false
	    && bundleItem.calculatedBundleItem == true
	    && document.getElementById(priceElemId)
        && bundleItem.priceTiers.length > 0 ) {

        var displayItemPrice = bundleItem.price;

        // Oops! It's a recurring item!
        var scheduleText = "";
        if (bundleItem.paymentSchedule.length > 0) {
            displayItemPrice = bundleItem.recurringPrice;
            scheduleText = " " + bundleItem.paymentSchedule;
        }

        // the bundle price was calculated on server side, taking the line price as 0 if it's negative.
        var adjustedLinePrice = (displayItemPrice + bundleItem.priceAdjustment) * bundleItem.quantity;
        if (adjustedLinePrice < 0) {
            displayItemPrice = 0;
        }
        var itemPriceString = currencySymbol + displayItemPrice.toFixed(2);
        document.getElementById(priceElemId).innerHTML = itemPriceString;
        document.getElementById(priceFrequencyElemId).innerHTML = scheduleText;

        // display item price adjustment only when displayItemPrice > 0
        if (displayItemPrice > 0
            && bundleItem.priceAdjustment
            && bundleItem.priceAdjustment != 0) {
        	document.getElementById(adjustmentElemId).innerHTML = formatPriceAdjustment(currencySymbol, bundleItem.priceAdjustment);
            document.getElementById(adjustmentFrequencyElemId).innerHTML = scheduleText;
        } else {
        	document.getElementById(adjustmentElemId).innerHTML = "";
            document.getElementById(adjustmentFrequencyElemId).innerHTML = "";
        }
    }

    if (bundleItem.constituents.length > 0) {
        for (index in bundleItem.constituents) {
            updateItemPrices(bundleItem.constituents[index]);
        }
    }
}

// we shall display quantity only when it > 1
function getQuantityText(quantity) {
    if (quantity > 1) {
        return " x " + quantity + " ";
    }

    return " ";
}

// format the price adjustment, add a dollar sign
// -10 => "-$10.00"
function formatPriceAdjustment(currencySymbol, priceAdjustment) {
    if (priceAdjustment > 0) {
        return currencySymbol + priceAdjustment.toFixed(2);
    } else if (priceAdjustment == 0) {
        return "";
    } if (priceAdjustment < 0) {
        return "-" + currencySymbol + Math.abs(priceAdjustment).toFixed(2);
    }
}

/*
Update the tiered bundle price display based on the contents of the top level tiers.

*/
function updateBundleTierPriceDisplay(bundleAvailable) {

	// clear out the existing productPrice html
	var priceDiv = document.getElementById("productPrice");

	if(priceDiv == null) {
		return;
	}

	while (priceDiv.hasChildNodes()) {

		priceDiv.removeChild(priceDiv.lastChild);

	}

	priceDiv.innerHTML = "";

    // If not all item in the bundle have price defined, we shall not display bundle price.
    if (bundleAvailable == false) {
        return;
    }

    displayAggregatedPricesForBundle(priceDiv)

}

function displayAggregatedPricesForBundle(priceDiv) {

    if (bundle.aggregatedPrices.length == 1) {
        priceDiv.innerHTML = "<div class='tier'><span class='reg-price'>&nbsp;" + bundle.aggregatedPrices[0].priceString + "</span></div>";
        return;
    }

	// add div with class tier
	var priceTierHtml = "";

	for ( var i = 0; i < bundle.aggregatedPrices.length; i++) {

		priceTierHtml = priceTierHtml + "<div class='tier'>";
		// inside this add a span with class tier-level for 'qty @'

        var tierQuantityRange = bundle.aggregatedPrices[i].minQty;

        if (i == bundle.aggregatedPrices.length-1) {
            tierQuantityRange += " +";
        } else if (bundle.aggregatedPrices[i+1].minQty-1 > bundle.aggregatedPrices[i].minQty) {
            tierQuantityRange += " - " + (bundle.aggregatedPrices[i+1].minQty-1);
        }
        tierQuantityRange += " @";

		priceTierHtml += " <span class='tier-level'> " + tierQuantityRange + "</span>";

		// add a span with class reg-price for the dollar amount.
		priceTierHtml += "<span class='reg-price'>&nbsp;" + bundle.aggregatedPrices[i].priceString + "</span>";

		priceTierHtml += "</div>";

	}

	priceDiv.innerHTML = priceTierHtml;

}

/*
Remove duplicates from the array argument, treating the first encountered element as the original.


*/
function uniqueArray(a)
{
   var r = new Array();
   o:for(var i = 0, n = a.length; i < n; i++)
   {
      for(var x = 0, y = r.length; x < y; x++)
      {
         if(r[x]==a[i]) continue o;
      }
      r[r.length] = a[i];
   }
   return r;
}

