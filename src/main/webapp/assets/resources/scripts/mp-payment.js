
function showPayment(jobRequestId) {

    if ($(".brick-payment").is(":hidden")) {
        $('.brick-payment').fadeIn();
        $('.brick-status').fadeIn();

    }
    else{
        $('.brick-payment').fadeOut();
        $('.brick-status').fadeOut();
        return;
    }

    const renderPaymentBrick = async (bricksBuilder) => {
        const settings = {
            initialization: {
                amount: 0.5,
            },
            customization: {
                paymentMethods: {
                    ticket: "all",
                    bankTransfer: "all",
                    creditCard: "all",
                    debitCard: "all",
                    mercadoPago: "all",
                },
            },
            callbacks: {
                onReady: () => {},
                onSubmit: ({
                               selectedPaymentMethod,
                               formData
                           }) => {
                    console.log(jobRequestId)
                    // callback chamado ao clicar no botão de submissão dos dados
                    return new Promise((resolve, reject) => {
                        fetch("minha-conta/cliente/pagamento", {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                            },
                            body: JSON.stringify(formData),
                        })
                            .then(response => {
                                if (!response.ok) {
                                    return response.json().then(responseBody => {
                                        throw new Error(responseBody.message);
                                    });
                                }

                                return response.json();
                            })
                            .then((response) => {
                                const paymentId = response.data.id;
                                renderStatusScreenBrick(bricksBuilder, paymentId);
                                // sendPaymentVoucher(paymentId);
                                paymentJobRequest(paymentId, jobRequestId);
                                resolve();
                            })
                            .catch((error) => {
                                swal({
                                    title: "Opss",
                                    text: error.message,
                                    icon: "error",
                                });
                                reject();
                            });
                    });
                },
                onError: (error) => {
                    console.error(error);
                },
            },
        };

        bricksBuilder.create(
            "payment",
            "paymentBrick_container",
            settings
        )
            .then(paymentBrickController => {

            })
            .catch(error => {
                console.error(error);
            });
    };

    renderPaymentBrick(bricksBuilder);
}


const renderStatusScreenBrick = async (bricksBuilder, paymentId) => {
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

    window.statusScreenBrickController = await bricksBuilder.create(
        'statusScreen',
        'statusScreenBrick_container',
        settings,
    );
};

function paymentJobRequest(paymentId, jobRequestId) {
    const URL = "/servicebook/minha-conta/cliente/pagamento/jobRequest";

    var dto = {
        id: null,
        jobRequestId: jobRequestId,
        paymentId: 1,
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
        alert("Houve um problema, não foi possível seguir.");
    });
}
