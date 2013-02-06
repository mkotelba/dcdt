//Copy the Email Address:
$(document).ready(function ()
{
	$("#copyAddress").click(function ()
	{
		//gets the items without any seperater
		var copyAdd = $('#selectEmail option:selected').text();
		document.getElementById("seldropDown").value = copyAdd;
		alert("selected value " + copyAdd);

	});
});

//Function to set the drop down
function setDropText(select_ele)
{
	// Ignore "None"
	if (select_ele.selectedIndex == 0)
	{
		return;
	}
	document.getElementById("seldropDown").innerHTML = select_ele.options[select_ele.selectedIndex].text;

}

function setText(select_ele)
{
	var commentsElement = $("#comments");
	commentsElement.empty();
	
	var discoveryTestcaseId = select_ele.options[select_ele.selectedIndex].value;
	var discoveryTestcase = window.DISCOVERY_TESTCASES.hasOwnProperty(discoveryTestcaseId) ? 
		window.DISCOVERY_TESTCASES[discoveryTestcaseId] : null;
	
	if (discoveryTestcase)
	{
		var discoveryTestcaseComments = discoveryTestcase.comments;
		
		appendTestcaseCommentsSection(commentsElement, "Purpose/Description", discoveryTestcaseComments.description, false);
		appendTestcaseCommentsSection(commentsElement, "Additional Info", discoveryTestcaseComments.additionalInfo, false);
		appendTestcaseCommentsSection(commentsElement, "Target Certificate", discoveryTestcaseComments.targetCert, false);
		appendTestcaseCommentsSection(commentsElement, "Background Certificates", discoveryTestcaseComments.backgroundCerts, true);
		appendTestcaseCommentsSection(commentsElement, "RTM", discoveryTestcaseComments.rtm, false);
		appendTestcaseCommentsSection(commentsElement, "Underlying Specification Reference", discoveryTestcaseComments.specifications, true);
	}
}
