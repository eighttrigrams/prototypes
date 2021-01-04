defmodule Plain.Planning.Task do
  use Ecto.Schema
  import Ecto.Changeset

  schema "tasks" do
    field :name, :string

    many_to_many :users, Plain.Accounts.User, join_through: "users_tasks"
    belongs_to :goal, Plain.Planning.Goal
    timestamps()
  end

  @doc false
  def changeset(task, attrs) do
    task
    |> cast(attrs, [:name])
    |> validate_required([:name])
  end
end
