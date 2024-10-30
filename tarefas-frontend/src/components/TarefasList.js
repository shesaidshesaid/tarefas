// src/components/TarefasList.js

import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  fetchTarefas,
  deleteTarefa,
  updateTarefa,
} from '../store/actions/tarefasActions';
import { Table, Button, Typography, Image, Modal } from 'antd';
import {
  DeleteOutlined,
  EditOutlined,
  EyeOutlined,
  CheckOutlined,
} from '@ant-design/icons';
import TarefaEditForm from './TarefaEditForm';

const { Text } = Typography;

function TarefasList() {
  const dispatch = useDispatch();
  const { tarefas, loading, error } = useSelector((state) => state);

  const [editingTarefa, setEditingTarefa] = useState(null);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [imagePreview, setImagePreview] = useState(null);

  useEffect(() => {
    dispatch(fetchTarefas());
  }, [dispatch]);

  const handleDelete = (id) => {
    dispatch(deleteTarefa(id));
  };

  const handleEdit = (tarefa) => {
    setEditingTarefa(tarefa);
    setIsModalVisible(true);
  };

  const handleEditSubmit = (updatedTarefa) => {
    dispatch(updateTarefa(updatedTarefa));
    setIsModalVisible(false);
    setEditingTarefa(null);
  };

  const handleCancelEdit = () => {
    setIsModalVisible(false);
    setEditingTarefa(null);
  };

  const handleImagePreview = (fotoUrl) => {
    setImagePreview(`https://protected-gorge-65520-26115e348254.herokuapp.com${fotoUrl}`);
  };

  const handleImagePreviewClose = () => {
    setImagePreview(null);
  };

  const handleConcluir = (tarefa) => {
    dispatch(updateTarefa({ ...tarefa, concluida: true }));
  };

  const columns = [
    {
      title: 'Tarefas',
      dataIndex: 'titulo',
      key: 'titulo',
      render: (text, tarefa) => (
        <>
          <Text delete={tarefa.concluida} strong>
            {text}
          </Text>
          <br />
          <Text type="secondary">{tarefa.descricao}</Text>
        </>
      ),
    },
    {
      title: 'Status',
      key: 'status',
      render: (_, tarefa) => (
        <span>{tarefa.concluida ? 'Finalizado' : 'Pendente'}</span>
      ),
    },
    {
      title: 'Ações',
      key: 'acoes',
      render: (_, tarefa) => (
        <>
          {tarefa.fotoUrl && (
            <Button
              icon={<EyeOutlined />}
              onClick={() => handleImagePreview(tarefa.fotoUrl)}
            >
              Ver Foto
            </Button>
          )}
          <Button
            icon={<CheckOutlined />}
            onClick={() => handleConcluir(tarefa)}
            style={{
              backgroundColor: tarefa.concluida ? '#d9d9d9' : '#1890ff',
              color: tarefa.concluida ? 'grey' : 'white',
              cursor: tarefa.concluida ? 'not-allowed' : 'pointer',
              borderColor: tarefa.concluida ? '#d9d9d9' : '#1890ff',
              width: 100,
            }}
            disabled={tarefa.concluida}
          >
            {tarefa.concluida ? 'Concluído' : 'Concluir'}
          </Button>
          <Button
            type="primary"
            icon={<EditOutlined />}
            onClick={() => handleEdit(tarefa)}
          >
            Editar
          </Button>
          <Button
            icon={<DeleteOutlined />}
            onClick={() => handleDelete(tarefa.id)}
            style={{
              backgroundColor: '#e6f7ff',
              color: '#1890ff',
              borderColor: '#91d5ff',
            }}
          >
            Deletar
          </Button>
        </>
      ),
    },
  ];

  if (loading) {
    return <div>Carregando...</div>;
  }

  if (error) {
    return <div>Erro: {error}</div>;
  }

  return (
    <>
      <Table
        dataSource={tarefas}
        columns={columns}
        rowKey="id"
        pagination={false}
      />

      {editingTarefa && (
        <TarefaEditForm
          visible={isModalVisible}
          onCancel={handleCancelEdit}
          onEdit={handleEditSubmit}
          tarefa={editingTarefa}
        />
      )}

      {imagePreview && (
        <Modal
          visible={true}
          footer={null}
          onCancel={handleImagePreviewClose}
        >
          <Image src={imagePreview} alt="Foto da Tarefa" />
        </Modal>
      )}
    </>
  );
}

export default TarefasList;
