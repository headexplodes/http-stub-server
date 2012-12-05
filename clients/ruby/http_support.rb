require 'uri'
require 'httpclient'

module HttpSupport
  attr_accessor :response
  
  def http_client
    @http_client ||= HTTPClient.new
  end

  def within_base_uri(uri)
    yield (@base_url && !(uri =~ /#{@base_url}/) ? File.join(@base_url, uri) : uri)
  end

  def with_oauth_access_token
    @append_oauth2_access_token = true
    result = yield
    @append_oauth2_access_token = false
    result
  end

  def get(uri, params=nil, header=nil, *args)
    @response = within_base_uri(uri) do |uri|
      # We are doing this so we can support array of values inside a map, e.g.
      # {:key => [1,2,3]}
      # This is because httpclient does not support his out of the box
      uri = "#{uri}?#{URI.encode_www_form(params)}" if params
      http_client.get(uri, nil, append_oauth_access_token_header(header), *args)
    end
  end

  def post(uri, body=nil, header=nil, *args)
    @response = within_base_uri(uri) do |uri|
      header = append_oauth_access_token_header(header)
      header = append_validate_only_header(header)
      http_client.post(uri, body, header, *args)
    end
  end

  def post_json(uri, body=nil, header=nil, *args)
    header = (header ? header : {}).merge({'Content-Type' => 'application/json'})
    post(uri, body, header, *args)
  end
  
  def delete(uri, body=nil, header = nil, *args)
    @response = within_base_uri(uri) do |uri|
      header = append_oauth_access_token_header(header)
      header = append_validate_only_header(header)
      http_client.delete(uri, body, header, *args)
    end
  end

  def response_code
    response.code
  end

  def response_json
    json_response? ? JSON.parse(response.body) : nil
  end

  def response_text
    text_response? ? response.body : nil
  end

  def json_response?
    !(response.content_type =~ /application\/json/).nil?
  end

  def text_response?
    !(response.content_type =~ /text\/plain/).nil?
  end

end
