defmodule Plain.Repo do
  use Ecto.Repo,
    otp_app: :plain,
    adapter: Ecto.Adapters.Postgres
end
