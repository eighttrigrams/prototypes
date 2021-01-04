defmodule Plain.Planning.Goal do
  use Ecto.Schema
  import Ecto.Changeset

  schema "goals" do
    field :name, :string

    has_many :tasks, Plain.Planning.Task
    timestamps()
  end

  @doc false
  def changeset(goal, attrs) do
    goal
    |> cast(attrs, [:name])
    |> validate_required([:name])
  end
end
