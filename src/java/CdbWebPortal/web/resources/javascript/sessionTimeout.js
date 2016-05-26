function handleSessionTimeout() 
{
    console.log('Handling session timeout')
    setTimeout(loadHomeView, 1000);  // wait for a second before loading home page
}

function loadHomeView() 
{
    console.log('Loading home window after session timeout')
    window.location.replace('../home.xhtml');
}
