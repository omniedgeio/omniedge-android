package io.omniedge

fun StringBuilder.apnd(str: String, predict: () -> Boolean = { true }): StringBuilder {
    return if (predict()) {
        when {
            this.endsWith(" ") -> {
                this.append(str)
            }
            else -> {
                this.append(" ")
                this.append(str)
            }
        }
    } else {
        this
    }
}

fun StringBuilder.insrt(str: String, predict: () -> Boolean = { true }): StringBuilder {
    return if (predict()) {
        when {
            this.startsWith(" ") -> {
                this.insert(0, str)
            }
            else -> {
                this.insert(0, " ")
                this.insert(0, str)
            }
        }
    } else {
        this
    }
}