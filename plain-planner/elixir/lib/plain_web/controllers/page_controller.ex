defmodule PlainWeb.PageController do
  use PlainWeb, :controller

  def index(conn, _params) do
    render(conn, "index.html")
  end
end
