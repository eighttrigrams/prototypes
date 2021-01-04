defmodule PlainWeb.UserView do
  use PlainWeb, :view

  def render("user.json", %{user: user}) do
    %{name: user.name}
  end
end
