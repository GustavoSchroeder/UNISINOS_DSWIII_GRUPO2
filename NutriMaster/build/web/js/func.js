
function rolarTela() {
    var posicao = document.getElementById("formProposta:painelRiscoQLOperacao");
    posicao.scrollIntoView();
}

function setFocus() {
    document.getElementById("painelRiscoQLOperacao").focus();
}

function numeros(ie, ff) {
    if (ie) {
        tecla = ie;
    } else {
        tecla = ff;
    }

    /**
     * 13 = [ENTER]
     * 8  = [BackSpace]
     * 9  = [TAB]
     * 46 = [Delete e Ponto]
     * 44 = [Virgula]
     * 48 a 57 = São os números
     */
    if ((tecla >= 44 && tecla <= 57)) {
        return true;
    } else {
        return false;
    }
}

function onlychars(e)
{
    var tecla = new Number();
    if (window.event) {
        tecla = e.keyCode;
    } else if (e.which) {
        tecla = e.which;
    } else {
        return true;
    }
    if ((tecla >= "48") && (tecla <= "57")) {
        return false;
    }
}

function AplicarMascaraCpfCnpj(objTextBox) {
    var pComponente = objTextBox;
    var campoMask, noTexto, noTextoMascara = '', nonNumbers = /\D/;

    noTexto = pComponente.value.toString();

    while (noTexto != noTexto.replace('.', '')) {
        noTexto = noTexto.replace('.', '');
    }
    while (noTexto != noTexto.replace('-', '')) {
        noTexto = noTexto.replace('-', '');
    }
    while (noTexto != noTexto.replace('/', '')) {
        noTexto = noTexto.replace('/', '');
    }

    if (nonNumbers.test(noTexto)) {
        return;
    }

    if (noTexto.length <= 11)
    {
        while (noTexto.length != 11)
        {
            noTexto = '0' + noTexto;
        }
        for (var intContador = 0; intContador < noTexto.length; intContador++)
        {
            noTextoMascara += noTexto.charAt(intContador);
            if (intContador == 2 || intContador == 5)
            {
                noTextoMascara += '.';
            } else if (intContador == 8)
            {
                noTextoMascara += '-';
            }
        }
    } else
    {
        if (noTexto.length > 14)
        {
            noTexto = noTexto.substr(0, 14);
        }
        while (noTexto.length != 14)
        {
            noTexto = '0' + noTexto;
        }
        for (var intContador = 0; intContador < noTexto.length; intContador++)
        {
            noTextoMascara += noTexto.charAt(intContador);
            if (intContador == 1 || intContador == 4)
            {
                noTextoMascara += '.';
            } else if (intContador == 7)
            {
                noTextoMascara += '/';
            } else if (intContador == 11)
            {
                noTextoMascara += '-';
            }
        }
    }

    pComponente.value = noTextoMascara;
}


