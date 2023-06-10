using JuMP, CPLEX

include("param.txt")

m = Model(CPLEX.Optimizer)
set_attribute(m, "CPX_PARAM_EPINT", 1e-8) # https://www.ibm.com/docs/en/icos/22.1.1?topic=parameters-integrality-tolerance
set_optimizer_attribute(m, "CPX_PARAM_EPGAP", 0.02) # https://www.ibm.com/docs/en/icos/22.1.1?topic=parameters-relative-mip-gap-tolerance
set_optimizer_attribute(m, "CPX_PARAM_SOLNPOOLCAPACITY", 1) # https://www.ibm.com/docs/en/icos/22.1.1?topic=parameters-maximum-number-solutions-kept-in-solution-pool


# matrice des deplacements (entre i et j lors du roud k)
@variable(m, 0 <= x[i = 1:n, j = 1:n, k = 1:r] <= 1, Int)

# distance parcourue par chaque equipe
@variable(m, dt[i = 1:n], Int)

# moyenne des distances
@variable(m, dtm)

# valeur maximale des dti
@variable(m, z, Int)


# fonction objective: minimisation de la valeur de z
@objective(m, Min, z)


# affectation de la valeur de z
@constraint(m, [i=1:n], z >= dt[i])

# affectation des valeurs des dti
@constraint(m, [i=1:n], dt[i] == sum(2*d[i,j]*x[i,j,k] for j in 1:n, k in 1:r))

# affectation de la valeur de dtm
@constraint(m, dtm == (1/n) * sum(2*d[i,j]*x[i,j,k] for i in 1:n, j in 1:n, k in 1:r))

# pas de match contre soit meme
@constraint(m, [i=1:n, k=1:r],  x[i,i,k] == 0)

# a chaque round, toutes les equipes jouent
@constraint(m, [j=1:n, k=1:r], sum(x[i,j,k] + x[j,i,k] for i in 1:n) == 1)

# chaque equipe affronte les autres 1 fois
@constraint(m, [i=1:n-1, j=i+1:n], sum(x[i,j,k] + x[j,i,k] for k in 1:r) == 1)

# limitation du nombre de match consecutif a l'exterieur
@constraint(m, [i=1:n, k=1:r-U_CONS], L_CONS <= sum(x[i,j,k+l] for l in 1:U_CONS, j=1:n) <= U_CONS)

# limitation du nombre de match à l'exterieur
@constraint(m, [i=1:n], L <= sum(x[i,j,k] for j in 1:n, k in 1:r) <= U)

status = optimize!(m)
println("Temps de resolution: ", solve_time(m))
println("Valeur objective: ", objective_value(m))
println("dtm: ", value(dtm))
println("z: ",value(z))
for dti in dt
    println("$(name(dti)) = $(value(dti))")
end
for xijk in x
    if(value(xijk) == 1)
        println("$(name(xijk)) = $(value(xijk))")
    end
end
#println("Modèle: ", m)