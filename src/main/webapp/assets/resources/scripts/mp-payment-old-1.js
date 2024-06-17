function showPayment(jobRequestId) {

    alert(jobRequestId)

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
                onReady: () => {
                    console.log("Payment brick is ready.");
                },
                onSubmit: ({ selectedPaymentMethod, formData }) => {
                    console.log("Form data submitted:", formData);
                    console.log("Selected payment method:", selectedPaymentMethod);
                    formData.paymentMethod = selectedPaymentMethod;
                    // Callback called on form submission
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
                            .then(response => {
                                const paymentId = response.data.id;
                                console.log("Payment successful, ID:", paymentId);
                                renderStatusScreenBrick(bricksBuilder, paymentId);
                                paymentJobRequest(paymentId, jobRequestId);
                                resolve();
                            })
                            .catch(error => {
                                console.error("Payment submission error:", error.message);
                                swal({
                                    title: "Opss",
                                    text: error.message,
                                    icon: "error",
                                });
                                reject(error);
                            });
                    });
                },
                onError: (error) => {
                    console.error("Payment brick error:", error);
                },
            },
        };

        try {
            const paymentBrickController = await bricksBuilder.create(
                "payment",
                "paymentBrick_container",
                settings
            );
            console.log("Payment brick created successfully.");
        } catch (error) {
            console.error("Error creating payment brick:", error);
        }
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