require_relative '../clients/stubbing_support'

class StubMessageBuilder
  attr_accessor :message # object that will be posted to stubby

  def initialize(stub_client)
    @stub_client = stub_client
    @message = {}
  end

  def path(path)
    request[:path] = convert_str(path)
    self
  end

  def method(method)
    request[:method] = convert_str(method)
    self
  end

  def get(path = nil)
    method('GET')
    path(path) unless path.nil?
    self
  end

  def post(path = nil)
    method('POST')
    path(path) unless path.nil?
    self
  end

  def delete(path = nil)
    method('DELETE')
    path(path) unless path.nil?
    self
  end

  def param(name, value)
    queryParams << {
        :name => convert_str(name),
        :value => convert_str(value)
    }
    self
  end

  def header(name, value)
    request_headers << {
        :name => convert_str(name),
        :value => convert_str(value)
    }
    self
  end

  def content_type(content_type)
    request[:contentType] = convert_str(content_type)
    self
  end

  def return_content_type(content_type)
    response[:contentType] = content_type
    self
  end

  def return_status(status_code)
    response[:statusCode] = status_code
    self
  end

  def return_header(name, value)
    response_headers << {
        :name => name,
        :value => value
    }
    self
  end

  def return_body(content)
    response[:body] = content
    self
  end

  def return(status_code, body)
    return_status(status_code)
    return_body(body)
    if body.kind_of?(Hash)
        return_content_type('application/json')
    else
        return_content_type('text/plain')
    end
    self
  end

  def stub!
    raise 'Missing request path' if @message[:request].nil? || @message[:request][:path].nil?
    raise 'Missing response code' if @message[:response].nil? || @message[:response][:statusCode].nil?
    @stub_client.stub_message!(@message)
  end

  private

  def queryParams
    request[:queryParams] = [] unless @message.key?(:queryParams)
    request[:queryParams]
  end

  def request_headers
    request[:headers] = [] unless @message.key?(:headers)
    request[:headers]
  end

  def response_headers
    response[:headers] = [] unless @message.key?(:headers)
    response[:headers]
  end

  def request
    @message[:request] = {} unless @message.key?(:request)
    @message[:request]
  end

  def response
    @message[:response] = {} unless @message.key?(:response)
    @message[:response]
  end

  def convert_str(obj) # allow use of regexp literals for readability
    obj.source if obj.kind_of?(Regexp)
    obj.to_s
  end

end
