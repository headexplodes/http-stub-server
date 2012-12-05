require 'json'

require_relative 'http_support'
require_relative 'capi_urls'
require_relative 'stub_message_builder'

module StubbingSupport
  include CapiUrls
  include HttpSupport

  def last_stub(index = 0)
    with_stub_check do
      get("#{stub_base_url}/_control/requests/#{index}")
    end
    response_json.to_sym_keys
  end

  def stub_message!(message)
    with_stub_check do
      post_json("#{stub_base_url}/_control/responses", JSON.pretty_generate(message))
    end
  end

  def with_stub_check
    yield
    raise "Error calling stub. Got HTTP #{response.code} response." unless response && response.code == 200
  end

  def stub!(path)
    request = default_stub_request_for(path)
    response = default_stub_response
    yield request, response
    generate_stub_json_response!(response)
    stub_message!(:request => request, :response => response)
  end

  def default_stub_request_for(path)
    {
      :method => 'GET',
      :path => path,
      :headers => [],
      :queryParams => [],
    }
  end

  def default_stub_response
    {
      :statusCode => 200,
      :contentType => 'application/json',
      :body => {},
    }
  end

  def generate_stub_json_response!(response)
    return response unless response[:contentType] =~ /^application\/json/

    response_body = response[:body]
    response[:body] = response_body.is_a?(String) ? response_body : JSON.pretty_generate(response_body)
    response
  end

  def stub_builder
    StubMessageBuilder.new(self)
  end
end