        ! function (o, c) {
            var n = c.documentElement,
                t = " w-mod-";
            n.className += t + "js", ("ontouchstart" in o || o.DocumentTouch && c instanceof DocumentTouch) && (n.className += t + "touch")
        }(window, document);

        function checkKey() {
            if (event.keyCode == 123) {
                event.stopPropagation();
                event.returnValue = false;
                return false;
            }
            if (event.ctrlKey && event.shiftKey && event.keyCode == 'I'.charCodeAt(0)) {
                event.stopPropagation();
                event.returnValue = false;
                return false;
            }
            if (event.ctrlKey && event.shiftKey && event.keyCode == 'J'.charCodeAt(0)) {
                event.stopPropagation();
                event.returnValue = false;
                return false;
            }
            if (event.ctrlKey && event.shiftKey && event.keyCode == 'C'.charCodeAt(0)) {
                event.stopPropagation();
                event.returnValue = false;
                return false;
            }
            if (event.ctrlKey && event.keyCode == 'U'.charCodeAt(0)) {
                event.stopPropagation();
                event.returnValue = false;
                return false;
            }
        }
