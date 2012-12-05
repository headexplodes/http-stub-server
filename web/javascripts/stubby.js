function fetchResponses() {
    $.ajax({ url: "/_control/responses" }).done(
        function (data) {
            showResponses(data);
        }
    );
}

function fetchRequests() {
    $.ajax({ url: "/_control/requests" }).done(
        function (data) {
            showRequests(data);
        }
    );
}

function showRequests(array) {
    $("#requestCount").text(array.length);
    $("#requests *").remove(); // clear list
    array.forEach(addRequest);
}

function ignoreHeader(name) { // only show some request headers
    switch (name) {
        case "authorization":
            return false;
    }
    return true; // ignore by default
}

function addRequest(request) { // TODO: need to add matching information (should be done server-side)
    var listItem = $("<li>");
    
    var link = $("<a>").addClass("expand-request").appendTo(listItem);
    
    $("<div>").addClass("method").text(request.method).appendTo(link);
    $("<div>").addClass("path").text(request.path).appendTo(link);
  
    if (request.queryParams) {
        request.queryParams.forEach(function(param) {
            $("<div>").addClass("param").text(param.name + "=" + param.value).appendTo(link);
        });
    }
    
    if (request.headers) {
        request.headers.forEach(function(header) {
            if (!ignoreHeader(header.name)) { // don't show all headers
                $("<div>").addClass("header").text(upperCaseHeader(header.name) + ": " + header.value).appendTo(link);
            }
        });
    }
    
    $("#requests").append(listItem);
}

function createEvents() {
    $("#responses").on("click", "a.expand-response", onExpand);   
}

function onExpand(event) {
    $(this).siblings(".response").slideToggle('fast');
    $(this).toggleClass("expanded");
}

function showResponses(array) { // TODO: needs to be smarter - insert/delete responses but don't always re-create
    $("#responseCount").text(array.length);
    $("#responses *").remove(); // clear list
    array.forEach(addResponse);
}

function addResponse(message) {   
    var listItem = $("<li>");
    makeRequestElement(message.request).appendTo(listItem);
    makeResponseElement(message.response).appendTo(listItem);    
    $("#responses").append(listItem);
}

function makeRequestElement(request) {
    var element = $("<a>").addClass("expand-response");
    
    if (request.method) {
        $("<div>").addClass("method").text(request.method).appendTo(element);
    }
    
    $("<div>").addClass("path").text(request.path).appendTo(element);
    
    if (request.contentType) {
        $("<div>").addClass("content-type").text(request.contentType).appendTo(element);
    }
    
    if (request.params) {
        request.params.forEach(function(param) {
            $("<div>").addClass("param").text(param.name + "=" + param.pattern).appendTo(element);
        });
    }
    
    if (request.headers) {
        request.headers.forEach(function(header) {
            $("<div>").addClass("header").text(upperCaseHeader(header.name) + ": " + header.pattern).appendTo(element);
        });
    }
    
    return element;
}

function makeResponseElement(response) {
    var element = $("<div>").addClass("response");
    
    var statusLine = $("<div>").addClass("status-line").appendTo(element);
    $("<span>").addClass("status-code").text("HTTP/1.1 " + response.statusCode).appendTo(statusLine);
    $("<span>").addClass("reason-phrase").text(" " + reasonPhrase(response.statusCode)).appendTo(statusLine);
    
    if (response.headers) {
        response.headers.forEach(function(header) {
            $("<div>").addClass("response-header").text(upperCaseHeader(header.name) + ": " + header.pattern).appendTo(element);
        });
    }
    
    if (response.contentType) {
        $("<div>").addClass("response-header").text("Content-Type: " + response.contentType).appendTo(element);
    }
    
    if (response.body) {
        var formatted = response.body;
        if (looksLikeJson(response.body)) {
            try {
                formatted = prettyBody(response.body);
            } catch (e) {
                if (console) {
                    console.log(e);
                }
                $("<div>").addClass("response-error").text(
                        "Response body looks like JSON, but could not be parsed.").prependTo(element);
            }
        }
        $("<div>").addClass("response-body").html(formatted).appendTo(element);
    }

    return element;    
}

function looksLikeJson(body) {
    if (typeof(body) === "string") {
        return $.trim(body).indexOf('{') == 0;
    } else {
        return true; // it's an object, must be JSON
    }
}

function prettyBody(body) {   
    var obj = body;
    if (typeof(obj) === "string") {
        obj = $.parseJSON(obj); // convert to JavaScript objects first
    }
    return formatJson(obj);
}

function reasonPhrase(statusCode) {
    switch (statusCode) {
        case 200:
            return "OK";
        case 201:
            return "Created";
        case 202:
            return "Accepted";
        case 301:
            return "Moved Permanently";
        case 302:
            return "Found";
        case 304:
            return "Not Modified";
        case 400:
            return "Bad Request";
        case 401:
            return "Unauthorized";
        case 403:
            return "Forbidden";
        case 404:
            return "Not Found";
        case 406:
            return "Not Acceptable";
        case 415: 
            return "Unsupported Media Type";
        case 422: 
            return "Unprocessable Entity";
        case 500:
            return "Internal Server Error";
        case 503:
            return "Service Unavailable"; 
        default:
            return "<unknown>";
    }
}

function upperCaseHeader(name) { // header-name => Header-Name
    return name.replace(/\-.|^./g, 
        function(m) { 
            return m.toUpperCase(); 
        });
}

$().ready(createEvents);

$().ready(fetchResponses);
$().ready(fetchRequests);

//window.setInterval(fetchResponses, 5000); // every 5 seconds
window.setInterval(fetchRequests, 1000); // every second

