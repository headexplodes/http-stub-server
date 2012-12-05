require_relative 'capi_urls'
require_relative 'http_support'

class StubClient # client for GenericStubController
  include CapiUrls
  include HttpSupport

  def initialize(base_uri = stubby_base_url)
    @base_url = base_uri
  end

  def stub_message!(message)
    ensure_succesful! do
      post_json('/_control/responses', message.to_json)
    end
  end

  def stub_builder
    StubMessageBuilder.new(self)
  end

  def last
    ensure_succesful! do
      get('/_control/requests/0')
    end
    response_json.to_sym_keys
  end

  def requests
    ensure_succesful! do
      get('/_control/requests')
    end
    response_json.to_sym_keys
  end

  def assert_header(header_name, expected_value)
    matches = last[:headers].select do |header|
      header[:name] == header_name.downcase && header[:value] == expected_value
    end
    raise "Header not found: '#{header_name}: #{expected_value}'" if matches.empty?
  end

  def assert_query_param(param_name, expected_value)
    matches = last[:queryParams].select do |param|
      param[:name] == param_name && param[:value] == expected_value
    end
    raise "Query parameter not found: #{param_name}=#{expected_value}" if matches.empty?
  end

  def reset!
    [
      '/_control/requests',
      '/_control/responses',
    ].each do |uri|
      ensure_succesful! do
        delete(uri)
      end
    end
  end

  private

  def ensure_succesful!
    yield
    raise "Error calling stub. Got HTTP #{@response.code} response." unless successful_response?
  end

  def successful_response?
    @response.code >= 200 && @response.code < 300
  end

end