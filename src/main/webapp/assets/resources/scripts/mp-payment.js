const mercadoPagoPublicKey = document.getElementById("mercado-pago-public-key").value;
const mercadopago = new MercadoPago(mercadoPagoPublicKey);
let cardPaymentBrickController;
let statusScreenBrickController;
const jobRequestId = document.getElementById("jobRequestID").value;

async function loadPaymentForm() {
    const productCost = document.getElementById('amount').value;
    const settings = {
        initialization: {
            amount: productCost,
        },
        callbacks: {
            onReady: () => {
                console.log('brick ready')
            },
            onError: (error) => {
                console(JSON.stringify(error))
                console(error)
            },
            onSubmit: (cardFormData) => {
                proccessPayment(cardFormData)
            }
        },
        locale: 'pt-br',
        customization: {
            paymentMethods: {
                ticket: "all",
                bankTransfer: "all",
                creditCard: "all",
                debitCard: "all",
                mercadoPago: "all",
            },
            // paymentMethods: {
            //     maxInstallments: 5
            // },
            // visual: {
            //     style: {
            //         theme: 'dark',
            //         customVariables: {
            //             formBackgroundColor: '#1d2431',
            //             baseColor: 'aquamarine'
            //         }
            //     }
            // }
        },
    }

    const bricks = mercadopago.bricks();
    cardPaymentBrickController = await bricks.create('cardPayment', 'mercadopago-bricks-contaner__PaymentCard', settings);
}

const proccessPayment = (cardFormData) => {
    fetch("/servicebook/minha-conta/cliente/pagamento", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(cardFormData),
    })
        .then(response => {
            return response.json();
        })
        .then(result => {
            if(!result.hasOwnProperty("error_message")) {
                paymentJobRequest(result.id, jobRequestId);

                $('.container__payment').fadeOut(500);
                setTimeout(() => { $('.container__cart').show(500).fadeIn(); }, 500);
                renderStatusScreenBrick(cardFormData, result.id);

            }else{
                alert("entrou no else")
            }

        })
        .catch(error => {
        });
}

function paymentJobRequest(paymentId, jobRequestId) {
    const URL = "/servicebook/minha-conta/cliente/pagamento/jobRequest";

    var dto = {
        jobRequestId: jobRequestId,
        paymentId: paymentId,
    };

    fetch(URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(dto),
    }).then((response) => {
        console.log(response)
        if (response.ok) {
            return response.json();
        }
    }).then((data) => {
        console.log('ID do item salvo:', data);
    }).catch((error) => {
        console.error('Erro ao processar a solicitação:', error);
    });
}

const renderStatusScreenBrick = async (cardFormData, paymentId) => {
    const settings = {
        initialization: {
            paymentId: paymentId,
        },
        callbacks: {
            onReady: () => {
                /*
                    Callback chamado quando o Brick estiver pronto.
                    Aqui você pode ocultar loadings do seu site, por exemplo.
                */
            },
            onError: (error) => {
                console.error(error);
            },
        },
    };

    const bricks = mercadopago.bricks();
    statusScreenBrickController = await bricks.create(
        'statusScreen',
        'statusScreenBrick_container',
        settings,
    );
}
// Handle transitions
document.getElementById('checkout-btn').addEventListener('click', function(){
    $('.container__cart').fadeOut(500);
    setTimeout(() => {
        loadPaymentForm();
        $('.container__payment').show(500).fadeIn();
    }, 500);
});
