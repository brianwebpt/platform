$(document).ready(function () {
    $('#subscriptionList div.accordion-body').each(
            function (index) {
                if (index == 0) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            }
    );
    $('#subscriptionList a.accordion-toggle').click(
            function () {
                $(this).parent().next().toggle('blind');
            }
    );

    $(".key-generate-button").click(function () {
        var elem = $(this);
        jagg.post("/site/blocks/subscription/subscription-add/ajax/subscription-add.jag", {
            action:"generateAPIKey",
            name:elem.attr("data-name"),
            version:elem.attr("data-version"),
            provider:elem.attr("data-provider"),
            context:elem.attr("data-context"),
            application:elem.attr("data-application")
        }, function (result) {
            if (result.error == false) {
                window.location.reload();
            } else {
                $("#subscribe-button").html('Subscribe').addClass('green').removeClass('disabled').removeAttr('disabled');
                jagg.message(result.message);
            }
        }, "json");

        $(this).html('Please wait...').removeClass('green').addClass('disabled').attr('disabled', 'disabled');
    });
});