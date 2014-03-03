<table cellpadding="4" bgcolor="dbcddc" width="100%">
<tr><th align="left">IOC</th><!--<th align="left">DB Files</th>--><th align="left">Record Types</th>
<th align="center">Additional Search Terms</th></tr>
<tr><td valign="top" align="left" width="20%">
    <select name="iocDropDown[]" multiple size="10" onChange="setDBFileSelect()">
    </select>
</td>
<!-- not fully functional yet, so commenting out and using hidden instead
<td valign="top" align="left" width="30%">
    <select name="dbFileDropDown[]" multiple size="10">
    </select>
</td>
-->
<input type="hidden" name="dbFileDropDown[]" value="-1">
<td valign="top" align="left" width="20%">
    <select name="recTypeDropDown[]" size="10" multiple>
    </select>
</td>
<td width="30%">
<table>
<tr><td>PV:</td><td><input type="text" name="pvname" value="<?php echo $_SESSION['PVSearchChoices']->getPVName(); ?>"></td></tr>
<tr><td>Field:</td><td><input type="text" name="pvfieldname" value="<?php echo $_SESSION['PVSearchChoices']->getPVFieldName(); ?>"></td></tr>
<tr><td>Value:</td><td><input type="text" name="pvfieldvalue" value="<?php echo $_SESSION['PVSearchChoices']->getPVFieldValue(); ?>"></td></tr>
<tr><td colspan="2" align="right">* wildcard string allowed</td></tr>
</table>
</td>
</tr>
<tr>
<td align="center"><input type="submit" name="PV Search" value="PV Search"></td>
</tr>
</table>
