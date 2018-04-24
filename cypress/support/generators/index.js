import strings from './naughtyStrings.json'

export const sampleArr = arr => arr[Math.floor(Math.random() * arr.length)];

export const genString = (len, srcArr) => {
    const generate = (len, acc) => {
        if (acc.length >= len) {
            var nghtStr = acc.substring(0, len);
            let curlyOpen = nghtStr.indexOf('{');
            let curlyClose = nghtStr.indexOf('}');
            // Cypress evaluates {...} as a key press which causes wrong error situation.
            if (curlyOpen < curlyClose) {
                // Break this by replacing closing paren by something else.
                return nghtStr.replace('}', '#');
            }
            return nghtStr;
        }

        return generate(len, `${acc} ${sampleArr(srcArr)}`);
    };

    return generate(len, sampleArr(srcArr));
};

export const genNaughtyString = len => {
    return genString(len, strings);
};
