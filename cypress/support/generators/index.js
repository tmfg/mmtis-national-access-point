import strings from './naughtyStrings.json'

export const sampleArr = arr => arr[Math.floor(Math.random() * arr.length)];

export const genString = (len, srcArr) => {
    const generate = (len, acc) => {
        if (acc.length >= len) {
            return acc.substring(0, len);
        }

        return generate(len, `${acc}${sampleArr(srcArr)}`);
    };

    return generate(len, sampleArr(srcArr));
};

export const genNaughtyString = (len) => {
    const generate = (len, acc) => {
        if (acc.length >= len) {
            return acc.substring(0, len);
        }

        return generate(len, `${acc} ${sampleArr(strings)}`);
    };

    return generate(len, sampleArr(strings));
};


