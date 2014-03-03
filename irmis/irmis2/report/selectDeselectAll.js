function selectDeselect(formId, isChecked)
{
	var form = document.getElementById(formId);//represents the form

	for(var i = 0; i < form.length; i++)
	{
		form.elements[i].checked = isChecked;
	}
}