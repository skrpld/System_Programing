fun shopping(
    money: Int, // Our money
    price: Int // Price of chocolate
): Int {
    var wrap = 0    // Num of accumulated wraps
    var result = 0

    if (money >= price) {
        wrap += money / price
        result += wrap
        while (wrap >= 3) {
            result += wrap / 3
            wrap = (wrap / 3) + (wrap % 3)
        }
        return result
    } else {
        return 0
    }
}
