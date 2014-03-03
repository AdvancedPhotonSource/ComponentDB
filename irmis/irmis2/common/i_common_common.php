<?php
/*
 * PHP Common Include
 * Common file for header of all top-level php pages of APS PHP applications.
 * Defines some basic behavior that should be common to all applications.
 * NOTE: this php MUST be included at the top of a page before ANY HTML
 * or HTTP headers are shown.
 */
session_start();
header("Cache-control: private");

?>