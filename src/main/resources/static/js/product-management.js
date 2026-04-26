let displayedDescMaxCharCount = 200;
window.addEventListener('load', function () {
    let displayedDescription = document.getElementById('displayedDescription');
    let displayedDescriptionWrapper = displayedDescription.parentElement;
    updateRemainingCharacterCount(displayedDescriptionWrapper, displayedDescMaxCharCount)
    displayedDescription.addEventListener('input', function () {
        updateRemainingCharacterCount(displayedDescriptionWrapper, displayedDescMaxCharCount);
    });
});

function updateRemainingCharacterCount(textAreaWrapper, maxCharCount) {
    if (!textAreaWrapper || !maxCharCount) {
        return;
    }
    let textAreaElement = textAreaWrapper.querySelector('textarea');
    let charCountElement = textAreaWrapper.querySelector('#charCount');
    let currentCharCount = textAreaElement.value.length;
    let remainingCharCount = maxCharCount - currentCharCount;
    charCountElement.textContent = `${remainingCharCount}`;
}