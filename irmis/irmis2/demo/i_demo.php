<table cellpadding="4" bgcolor="dbcddc" width="100%">
<tr><th align="left">IOC List</th></tr>
<tr><td valign="top" align="left" width="50%">
    <select name="iocSelectList" multiple size="10">
       <?php
          $iocList = $_SESSION['iocList'];
          $iocEntities = $iocList->getArray();
          foreach ($iocEntities as $iocEntity) {
            echo '<option name="'.$iocEntity->getIocId().'">'.$iocEntity->getIocName().'</option>';
          }
       ?>
    </select>
</td>
<tr>
<td align="left">Enter ioc name (blank or * allowed):<br>
<input type="text" name="iocNameConstraint" value="<?php echo $_SESSION['iocNameConstraint'] ?>"></td>
</tr>
<tr>
<td align="left"><input type="submit" name="IOC Search" value="IOC Search"></td>
</tr>
</table>
