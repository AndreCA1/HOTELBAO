# Resumo do trabalho

- Quartos são alugados sempre somente por 1 dia

## Perfis

1. cliente (_autenticado_)

   - criar conta
   - fazer reserva (se quarto livre)
   - ver relatórios gerenciais
   - recuperar senha

2. administrador (_autenticado?_)

   - mesmas funcionalidades de cliente
   - CRUD quartos
   - CRUD clientes (senha padrão é o telefone do cliente)

3. usuário (_não autenticado_)

   - listar quartos

## Infos ao usuário

1. Valor total da estadia/cliente
2. relatorio dos dados cadastrados dos: clientes, quartos e estadias
3. estadia mais cara/cliente
4. estadia mais barata/cliente
5. emissão de cupom fiscal

## Possibilidade de deleção fisica do banco de dados

- ao se deletar totalmente o banco de dados deve-se
  mostrar uma mensagem de confirmação (em vetores, zere
  em strings, insira null)

## OBRIGATORIO

- Documentação Swagger para todos os endpoints, com o maximo de informação possivel
- Hateos para todos os endpoints do projeto
- Cados de teste para todos os endpoints do projeto

### Outputs

1. Listagem/Relatório de clientes cadastrados

   - deve haver mensagem de confirmação de impressão
   - Caso não haja clientes: “Não existem clientes cadastrados no sistema!”

2. Listagem/Relatório de quartos cadastrados

   - deve haver mensagem de confirmação de impressão
   - Caso não haja clientes: “Não existem quartos cadastrados no sistema!”

3. Listagem/Relatório de estadias cadastradas

   - deve haver mensagem de confirmação de impressão
   - Caso não haja clientes: “Não existem estadias lançadas no sistema!”

4. impressão de nota fiscal

   - deve-se imprimir os dados do cliente escolhido, as estadias realizadas
     e a soma total das estadias
   - O sistema deve listar todos os clientes e deixar que o usuário escolha
     o cliente a se imprimir a nota fiscal ??Todos os dados do cliente devem ser
     exibidos, senão uma mensagem deve ser mostrada informando a obrigatoriedade
     dos dados e, por fim, a operação deve ser abortada.??
   - O cliente deve ter ao menos uma estadia com valor e descrição informados,
     caso contrário, aborte a operação e informe a obrigatoriedade dos dados
   - Somente quartos com descrição e valor devem ser impressos e totalizados
