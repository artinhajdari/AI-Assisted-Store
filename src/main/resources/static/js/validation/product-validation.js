import JustValidate from "../lib/just-validate.es.js";
import {addFieldIfExists, requiredRule} from "../util/validation-utils.js";

window.addEventListener('load', function () {
    const validation = new JustValidate('#productForm');
    addFieldIfExists(validation, '#name', [
        requiredRule()
    ]);
    addFieldIfExists(validation, '#price', [
        requiredRule(),
        {
            rule: 'minNumber',
            value: parseFloat(validationData.minPrice) || 0.01,
            errorMessage: validationData.minPriceMessage
        },
        {
            rule: 'maxNumber',
            value: parseInt(validationData.maxPrice) || 10000,
            errorMessage: validationData.maxPriceMessage
        }
    ]);
    addFieldIfExists(validation, '#quantity', [
        requiredRule(),
        {
            rule: 'integer',
            errorMessage: validationData.integerQuantity
        },
        {
            rule: 'minNumber',
            value: parseInt(validationData.minQuantity) || 1,
            errorMessage: validationData.minQuantityMessage
        },
        {
            rule: 'maxNumber',
            value: parseInt(validationData.maxQuantity) || 1000,
            errorMessage: validationData.maxQuantityMessage
        }
    ]);
    addFieldIfExists(validation, '#aiDescription', [
        requiredRule(),
        {
            rule: 'minLength',
            value: parseInt(validationData.detailedDescMinLength) || 15,
            errorMessage: validationData.detailedDescMinLengthMessage
        }
    ]);
    addFieldIfExists(validation, '#displayedDescription', [
        requiredRule(),
        {
            rule: 'minLength',
            value: parseInt(validationData.simpleDescMinLength) || 10,
            errorMessage: validationData.simpleDescMinLengthMessage
        }
    ]);
    validation.onSuccess((event) => {
        event.target.submit();
    });
});