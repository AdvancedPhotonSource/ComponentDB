/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */

// Resolves the issue with touch devices not being able to use column filters. (Added in primefaces 6.2) 
$('.ui-column-filter').on('click touchend', function (e) {
    e.target.focus()   
});
