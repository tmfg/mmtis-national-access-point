export function randomInteger(range, min, max) {
    if( min ) {
        return Math.floor(Math.random() * (max - min + 1) ) + min;
    } else {
        return Math.floor(Math.random() * range);
    }
}

export function randomName(prefix) {
    return `${prefix}${randomInteger(100000)}`;
}

export function randomBusinessid() {
    const businessId = randomInteger(9999999, 1000000, 9999999) + '-' + randomInteger(9);
    return businessId;
}