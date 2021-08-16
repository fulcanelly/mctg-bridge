require 'sqlite3'
require 'json'


#  CREATE TABLE user_stats(
#     total_time LONG NOT NULL,
#     last_point LONG,
#     deaths LONG,
#     name STRING PRIMARY KEY NOT NULL
#);
#

class Hash
    def method_missing(name)
        self[name]
    end
end

class BoxedStats < Struct.new(:it) 

    def method_missing(name)
        [
            :total, :last, :deaths, :name
        ]
        .index(name).tap do |index|
            raise "no such column: %s" % name unless index
            return it[index]
        end
    end

end


class PlayerDatabaseWrapper < Struct.new :db

    def is_player_exists? player_name
        find_by_name player_name
    end
    
    def update old_stats 
        puts "updating :%p" % old_stats.name

        BoxedStats.new(find_by_name old_stats.name).tap do |new_stats|
            db.execute('UPDATE user_stats SET total_time = ?, last_point = ?, deaths = ? WHERE name = ?', 
                new_stats.total + old_stats.total, new_stats.last, new_stats.deaths + old_stats.deaths, old_stats.name)
        end
    end
    
    def create data
        puts "creating :%p" % data.name
        
        db.execute 'INSERT INTO user_stats VALUES(?, ?, ?, ?)', data.values
    end

    private

    def find_by_name(player_name)
        db.execute('SELECT * FROM user_stats WHERE name = ?', player_name).first
    end

end

class PlayerStatsDatabaseMigrator < Struct.new :file_path

    def get_data()
        JSON.parse File.read(file_path)   
    end

    def map_data(data)
        data.map do |name, stats|
            stats
                .merge({name: name})
                .map do |v, k|
                    [v.to_sym, k]
                end
                .to_h
        end

    end

    def merge_data(db, data)
        PlayerDatabaseWrapper.new(db).tap do |wrapper|
            data.each do |value| 
                wrapper
                    .method(if wrapper.is_player_exists? value[:name] then :update else :create end)
                    .call(value)
            end
        end
    end
    
    def move_to(db)
        map_data(get_data)
            .tap do |mapped|
                merge_data(db, mapped)
            end
    end

end

ARGV.first.tap do |server_home| 
    raise "Sepcify server folder" unless server_home 

    PlayerStatsDatabaseMigrator.new(server_home + '/plugins/tg-bridge/player-stats.json')
       .move_to SQLite3::Database.new(server_home + '/plugins/tg-bridge/database.sqlite3')
end
