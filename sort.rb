#!/bin/ruby
# A function to sort list of R packages, with columns algined to right.
#
# e.g.:
#
#           markdown    0.7.7
#       MatrixModels    0.4-1
#
if ARGV.size == 0
puts <<-EOS
  Usage: sort.rb {filename}
EOS
end
file = ARGV[0]

pkgs = {}
i = 0
File.open(file).drop(1).each do |line|
  m = /\s+([\w\.]+)\s+([\d\.-]+)/.match(line)
  if m == nil
    puts "ERROR:"
    puts line
    break
  end
  pkgs[m[1]] = m[2]
  i += 1
end
pkgs.sort_by { |k, v| k.downcase }.each do |k,v|
  puts "#{k}\t#{v}"
end
