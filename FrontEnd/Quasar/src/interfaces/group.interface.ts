export interface Group{
  id : number,
  name: string,
  description: string,
  picture?: string,
  owner_id?: number,
}

export function DefaultGroup(): Group {
  return {
    id: 0,
    name: '',
    description: '',
  };
}
