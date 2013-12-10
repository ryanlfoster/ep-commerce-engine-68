function hideSelectBox() {
	selects = document.getElementsByTagName("select");
    for (i = 0; i != selects.length; i++) {
        selects[i].style.visibility = "hidden";
    }
}

function showSelectBox() {
	selects = document.getElementsByTagName("select");
    for (i = 0; i != selects.length; i++) {
        selects[i].style.visibility = "visible";
    }
}