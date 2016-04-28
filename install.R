packages <- function(pkg){
      new.pkg <- pkg[!(pkg %in% installed.packages()[, "Package"])]
    if (length(new.pkg))
              install.packages(new.pkg, dependencies = TRUE, repos='http://cran.rstudio.com/', lib="/usr/lib/R")
        sapply(pkg, require, character.only = TRUE)
}

packages(c("Rserve","cluster","tables","fields","igraph","kernlab","lattice","MASS","mlbench","stats","proxy","fpc","clv","lars","kohonen","clusterGeneration","tnet"))
