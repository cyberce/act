function HashTable(obj) {
    this.length = 0;
    this.items = {};
    for (var p in obj) {
        if (obj.hasOwnProperty(p)) {
            this.items[p] = obj[p];
            this.length++;
        }
    }

    this.setItem = function (key, value) {
        var previous = undefined;
        if (this.hasItem(key)) {
            previous = this.items[key];
        }
        else {
            this.length++;
        }
        this.items[key] = value;
        return previous;
    }

    this.getItem = function (key) {
        return this.hasItem(key) ? this.items[key] : undefined;
    }

    this.hasItem = function (key) {
        return this.items.hasOwnProperty(key);
    }

    this.removeItem = function (key) {
        if (this.hasItem(key)) {
            previous = this.items[key];
            this.length--;
            delete this.items[key];
            return previous;
        }
        else {
            return undefined;
        }
    }

    this.keys = function () {
        var keys = [];
        for (var k in this.items) {
            if (this.hasItem(k)) {
                keys.push(k);
            }
        }
        return keys;
    }

    this.values = function () {
        var values = [];
        for (var k in this.items) {
            if (this.hasItem(k)) {
                values.push(this.items[k]);
            }
        }
        return values;
    }

    this.each = function (fn) {
        for (var k in this.items) {
            if (this.hasItem(k)) {
                fn(k, this.items[k]);
            }
        }
    }

    this.clear = function () {
        this.items = {}
        this.length = 0;
    }
}

function getRandomColor(format) {
    var rint = Math.round(0xffffff * Math.random());
    switch (format) {
        case 'hex':
            return ('#0' + rint.toString(16)).replace(/^#0([0-9a-f]{6})$/i, '#$1');
            break;
        case 'rgb':
            return 'rgb(' + (rint >> 16) + ',' + (rint >> 8 & 255) + ',' + (rint & 255) + ')';
            break;
        default:
            return rint;
            break;
    }
}

function populateZKTextbox(zkid, value) {
    var txtBox = zk.Widget.$('$' + zkid);
    txtBox.setValue(value);
    txtBox.smartUpdate('value', value);
}

