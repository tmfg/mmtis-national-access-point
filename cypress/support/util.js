export function randomInteger(range) {
    return Math.floor(Math.random() * range)
}

export function randomName(prefix) {
    return `${prefix}${randomInteger(100000)}`;
}
