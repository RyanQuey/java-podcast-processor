# destroys and re-creates all ES indices for all tables

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
bash $parent_path/destroy_indices.sh
bash $parent_path/create_all_indices.sh
