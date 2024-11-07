function MyFunction(){
	document.getElementById('weatherData').style.display = 'none';
}
function hideForm(){
	document.getElementById('exchangeRate').style.display = 'none';
}

function showComment() {
    var commentTextarea = document.getElementById('comment');
    commentTextarea.style.display = (commentTextarea.style.display === 'none' || commentTextarea.style.display === '') ? 'block' : 'none';
    
    var commentButton = document.getElementById('commButton');
    commentButton.style.display = (commentButton.style.display ==='none' || commentButton.style.display ==='')?'block' : 'none';
}
window.onload = function(){
	document.getElementById('comment').style.display='none';
}

