require 'date'

xml_file = ARGV[0] || "small.xml"

years = []
`export LC_ALL=C|< #{xml_file} parallel --pipe --block 10M grep -Po "'(?<=timestamp\\>)[^>]+(?=-[0-9]{2}-[0-9]{2}.*)'"`.each_line do |line|
	years << line.to_i
end
years = years.map{|res| res.to_i}.uniq.sort

period_length = (years.length / 3.0).ceil

very_old = years[0, period_length]
old = years[period_length, period_length]
recent = years[period_length*2 , period_length]

puts "Result of partitioning:"
puts "Very old: " + very_old.join(", ")
puts "Old: " + old.join(", ")
puts "Recent: " + recent.join(", ")
puts ""
puts "upBoundaryForVeryOld = " + Date.new(old.first).to_s
puts "   => timestamp < " + Date.new(old.first).to_s
puts "upBoundaryForOld = " + Date.new(recent.first).to_s
puts "   => " + Date.new(old.first).to_s + " <= timestamp < " + Date.new(recent.first).to_s
puts "recent: timestamp >= " + Date.new(recent.first).to_s
