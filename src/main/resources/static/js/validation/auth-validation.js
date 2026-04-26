import {addFieldIfExists, requiredRule, minLengthRule , maxLengthRule} from "../util/validation-utils.js";
import JustValidate from "../lib/just-validate.es.js";
window.onload = function () {
    const validation = new JustValidate('.validated-form');
    addFieldIfExists(validation, '#name', [
        requiredRule(),
        minLengthRule(parseInt(validationData.nameMinLength) || 3),
        maxLengthRule(parseInt(validationData.nameMaxLength) || 30),
    ]);
    addFieldIfExists(validation, '#surname', [
        requiredRule(),
        minLengthRule(parseInt(validationData.nameMinLength) || 3),
        maxLengthRule(parseInt(validationData.nameMaxLength) || 30),
    ]);
    addFieldIfExists(validation, '#username', [
        requiredRule(),
        {
            rule: 'email',
            errorMessage: validationData.validEmailAddress,
        },
    ]);
    addFieldIfExists(validation, '#password', [
        requiredRule(),
        minLengthRule(parseInt(validationData.passwordMinLength) || 6),
        maxLengthRule(parseInt(validationData.passwordMaxLength) || 50)
    ]);
    addFieldIfExists(validation, '#passwordConfirmation', [
        requiredRule(),
        {
            validator: (value, fields) => {
                return value === fields['#password'].elem.value;
            },
            errorMessage: validationData.matchingPasswords
        },
        minLengthRule(parseInt(validationData.passwordMinLength) || 6),
        maxLengthRule(parseInt(validationData.passwordMaxLength) || 50)
    ]);
    validation.onSuccess((event) => {
        event.target.submit();
    });
}

