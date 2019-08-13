function handleSessionTimeout(pathContextRoot) 
{
    console.log('Handling session timeout')
    setTimeout(loadHomeView(pathContextRoot), 1000);  // wait for a second before loading home page
}

function loadHomeView(pathContextRoot) 
{    
    console.log('Loading home window after session timeout')
    window.location.replace(pathContextRoot + '/index.xhtml');
}
