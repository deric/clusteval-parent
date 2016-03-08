packages <- function(pkg){
      new.pkg <- pkg[!(pkg %in% installed.packages()[, "Package"])]
    if (length(new.pkg))
              install.packages(new.pkg, dependencies = TRUE, repos='http://cran.rstudio.com/', lib="~/R/library")
        sapply(pkg, require, character.only = TRUE)
}

packages(c("Rserve","cluster","fields","kernlab","lattice","MASS","mlbench","stats","proxy","fpc","clv","lars","kohonen","clusterGeneration"))
