export const requiredRule = () => {
    return {
        rule: 'required',
        errorMessage: validationData.fieldRequired,
    }
}

export const minLengthRule = (length, errorMessage) => {
    return {
        rule: 'minLength',
        value: length,
        errorMessage: errorMessage
    }
}

export const maxLengthRule = (length, errorMessage) => {
    return {
        rule: 'maxLength',
        value: length,
        errorMessage: errorMessage
    }
}

export const addFieldIfExists = (validator, selector, rules) => {
    if (!document.querySelector(selector)) {
        return;
    }
    validator.addField(selector, rules);
}